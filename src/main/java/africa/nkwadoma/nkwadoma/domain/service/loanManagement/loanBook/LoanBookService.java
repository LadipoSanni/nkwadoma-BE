package africa.nkwadoma.nkwadoma.domain.service.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanManagement.*;
import africa.nkwadoma.nkwadoma.application.ports.input.loanManagement.loanBook.LoanBookUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanManagement.loanBook.RepaymentHistoryUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.loanBook.RepaymentHistoryOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.AsynchronousMailingOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanDecision;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.ModeOfPayment;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanBookService implements LoanBookUseCase {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final LoaneeOutputPort loaneeOutputPort;
    private final LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    private final IdentityManagerOutputPort identityManagerOutputPort;
    private final RepaymentHistoryOutputPort repaymentHistoryOutputPort;
    private final CohortUseCase cohortUseCase;
    private final AsynchronousMailingOutputPort asynchronousMailingOutputPort;
    private final LoaneeUseCase loaneeUseCase;
    private final RespondToLoanReferralUseCase respondToLoanReferralUseCase;
    private final ViewLoanReferralsUseCase viewLoanReferralsUseCase;
    private final CreateLoanProductUseCase createLoanProductUseCase;
    private final LoanRequestUseCase loanRequestUseCase;
    private final LoanOfferUseCase loanOfferUseCase;
    private final RepaymentHistoryUseCase repaymentHistoryUseCase;

    @Override
    public LoanBook upLoadFile(LoanBook loanBook) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanBook, "Loan book cannot be empty.");
        loanBook.validate();

        List<String[]> data = readFile(loanBook.getFile());
        log.info("Loan book read is {}", data);

        Cohort savedCohort = findCohort(loanBook.getCohort());
        List<Loanee> convertedLoanees = convertToLoanees(data, savedCohort);
        loanBook.setLoanees(convertedLoanees);
        referCohort(loanBook);
        completeLoanProcessing(loanBook);
        return loanBook;
    }
    @Override
    public void uploadRepaymentRecord(LoanBook repaymentRecordBook) throws MeedlException {
        MeedlValidator.validateObjectInstance(repaymentRecordBook, "Repayment record book cannot be empty.");
        repaymentRecordBook.validate();
        List<String[]> data = readFile(repaymentRecordBook.getFile());
        log.info("Repayment record book read is {}", data);

//        RepaymentHistory repaymentHistory =

        Cohort savedCohort = findCohort(repaymentRecordBook.getCohort());
        List<RepaymentHistory> convertedRepaymentHistories = convertToRepaymentHistory(data, savedCohort);
        List<RepaymentHistory> savedRepaymentHistories = saveRepaymentHistory(convertedRepaymentHistories, repaymentRecordBook.getActorId(), repaymentRecordBook.getCohort().getId());
    }

    private List<RepaymentHistory> saveRepaymentHistory(List<RepaymentHistory> repaymentHistories, String actorId, String cohortId) throws MeedlException {
        return repaymentHistoryUseCase.saveCohortRepaymentHistory(repaymentHistories, actorId, cohortId);
    }

    private void completeLoanProcessing(LoanBook loanBook) {
        loanBook.getLoanees()
                .forEach(loanee -> {
                    try {
                        LoanReferral loanReferral = acceptLoanReferral(loanee);
                        LoanRequest loanRequest = acceptLoanRequest(loanee, loanReferral, loanBook);
                        acceptLoanOffer(loanRequest);
                        startLoan(loanRequest);
                    } catch (MeedlException e) {
                        log.error("Error accepting loan referral.",e);
                    }
                });
    }

    private void startLoan(LoanRequest loanRequest) throws MeedlException {
        Loan loan = Loan.builder().loaneeId(loanRequest.getLoanee().getId()).loanOfferId(loanRequest.getId()).build();
        createLoanProductUseCase.startLoan(loan);
        log.info("Loan started for loanee {}", loanRequest.getLoanee().getUserIdentity().getEmail());
    }

    private void acceptLoanOffer(LoanRequest loanRequest) throws MeedlException {
        LoanOffer loanOffer = new LoanOffer();
        loanOffer.setId(loanRequest.getId());
        loanOffer.setLoaneeResponse(LoanDecision.ACCEPTED);
        loanOffer.setUserId(loanRequest.getLoanee().getUserIdentity().getId());
        loanOfferUseCase.acceptLoanOffer(loanOffer);
    }

    private LoanReferral acceptLoanReferral(Loanee loanee) throws MeedlException {
        LoanReferral loanReferral = LoanReferral.builder()
                .loanee(loanee)
                .build();
        loanReferral = viewLoanReferralsUseCase.viewLoanReferral(loanReferral);
        loanReferral.setLoanReferralStatus(LoanReferralStatus.ACCEPTED);
        respondToLoanReferralUseCase.respondToLoanReferral(loanReferral);
        return loanReferral;
    }
    private LoanRequest acceptLoanRequest(Loanee loanee, LoanReferral loanReferral, LoanBook loanBook) throws MeedlException {
        log.info("Loan Amount Approved is {}", loanee.getLoaneeLoanDetail().getAmountApproved());
        log.info("Amount received by this loanee {}", loanee.getLoaneeLoanDetail().getAmountReceived());
        LoanRequest loanRequest = LoanRequest.builder()
                //TODO Amount received should be changed to amount approved. Not currently been collected.
                .loanAmountApproved(loanee.getLoaneeLoanDetail().getAmountReceived())
                .loanAmountRequested(loanee.getLoaneeLoanDetail().getAmountRequested())
                .loanRequestDecision(LoanDecision.ACCEPTED)
                .id(loanReferral.getId())
                .loanProductId(loanBook.getLoanProductId())
                .loanee(loanee)
                .actorId(loanBook.getActorId())
                .referredBy(loanee.getReferredBy())
                .build();
        log.info("Accepting loan request for uploaded loanee {}", loanRequest);
        return loanRequestUseCase.respondToLoanRequest(loanRequest);
    }


    private void referCohort(LoanBook loanBook) {
        Iterator<Loanee> iterator = loanBook.getLoanees().iterator();
        while (iterator.hasNext()) {
            Loanee loanee = iterator.next();
            log.info("About to refer loanee with details {}", loanee);
            log.info("About to refer loanee in cohort with loan details {}", loanee.getLoaneeLoanDetail());
            try {
                inviteTrainee(loanee);
            } catch (MeedlException e) {
                log.error("Failed to invite trainee with id: {}", loanee.getId(), e);
                iterator.remove();
            }
        }

        log.info("Number of referable loanees :{} ",  loanBook.getLoanees().size());
//        asynchronousMailingOutputPort.notifyLoanReferralActors(loanBook.getLoanees());
    }
private void inviteTrainee (Loanee loanee) throws MeedlException {
    log.info("Single loanee is being referred...");
    loaneeUseCase.referLoanee(loanee);
}


    private Cohort findCohort(Cohort cohort) throws MeedlException {
        MeedlValidator.validateObjectInstance(cohort, CohortMessages.COHORT_CANNOT_BE_EMPTY.getMessage());
        return cohortUseCase.viewCohortDetails(cohort.getCreatedBy(), cohort.getId());
    }
    private List<RepaymentHistory> convertToRepaymentHistory(List<String[]> data, Cohort cohort) {
        List<RepaymentHistory> repaymentHistories = new ArrayList<>();

        log.info("Started creating Repayment record from data gotten from file upload {}, size {}",data, data.size());
        for (String[] row : data) {
            RepaymentHistory repaymentHistory = RepaymentHistory.builder()
                    .firstName(row[0].trim())
                    .lastName(row[1].trim())
                    .email(row[2].trim())
                    .paymentDate(row[3].trim())
                    .amountPaid(new BigDecimal(row[4].trim()))
                    .modeOfPayment(ModeOfPayment.valueOf(row[5].trim()))
                    .build();
            log.info("Repayment history {}", repaymentHistory);
            repaymentHistories.add(repaymentHistory);
        }
        return repaymentHistories;
    }


    private List<Loanee> convertToLoanees(List<String[]> data, Cohort cohort) {
        List<Loanee> loanees = new ArrayList<>();

        log.info("Started creating loanee data gotten from file upload {}, size {}",data, data.size());
        for (String[] row : data) {

            UserIdentity userIdentity = UserIdentity.builder()
                    .firstName(row[0].trim())
                    .lastName(row[1].trim())
                    .email(row[2].trim())
                    .phoneNumber(row[3].trim())
                    .dateOfBirth(row[4].trim())
                    .role(IdentityRole.LOANEE)
                    .createdAt(LocalDateTime.now())
                    .createdBy("73de0343-be48-4967-99ea-10be007e4347")
                    .build();
            log.info("Built user identity object with email {}", userIdentity.getEmail());

            LoaneeLoanDetail loaneeLoanDetail = LoaneeLoanDetail.builder()
                    .initialDeposit(new BigDecimal(row[5].trim()))
                    .amountRequested(new BigDecimal(row[6].trim()))
                    .amountReceived(new BigDecimal(row[7].trim()))
                    .build();

            log.info("Test values in the file {}", loaneeLoanDetail);
            Loanee loanee = Loanee.builder()
                    .userIdentity(userIdentity)
                    .loaneeLoanDetail(loaneeLoanDetail)
                    .loaneeStatus(LoaneeStatus.ADDED)
                    .onboardingMode(OnboardingMode.FILE_UPLOADED_FOR_DISBURSED_LOANS)
                    .cohortId(cohort.getId())
                    .build();
            log.info("Built loanee object with onboarding status {}", loanee.getOnboardingMode());
            log.info("Loanee object detail loan after creating loanee object {}", loanee.getLoaneeLoanDetail());

            loanees.add(loanee);
        }
        return savedData(loanees);
    }
    private List<Loanee> savedData(List<Loanee> loanees){
        List<Loanee> savedLoanees = new ArrayList<>();
        log.info("Started saving converted data of loanees");
        for (Loanee loanee : loanees){
            try {
                UserIdentity userIdentity = identityManagerOutputPort.createUser(loanee.getUserIdentity());
                userIdentityOutputPort.save(userIdentity);
                LoaneeLoanDetail savedLoaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loanee.getLoaneeLoanDetail());
                loanee.getLoaneeLoanDetail().setId(savedLoaneeLoanDetail.getId());
                log.info("Loanee's loan details after saving in file upload {}", savedLoaneeLoanDetail);
                loanee.setUserIdentity(userIdentity);
                loanee.setLoaneeLoanDetail(loanee.getLoaneeLoanDetail());

                Loanee savedLoanee = loaneeOutputPort.save(loanee);
                savedLoanee.getLoaneeLoanDetail().setAmountApproved(loanee.getLoaneeLoanDetail().getAmountApproved());
                log.info("Loanee's amount approved in file upload: {}", savedLoanee.getLoaneeLoanDetail());
                log.info("Loanee's actual loan details in file upload: {}", loanee.getLoaneeLoanDetail());
                savedLoanees.add(savedLoanee);
            } catch (MeedlException e) {
                log.info("Error occurred while saving data .", e);
                throw new RuntimeException(e);
            }
        }
        log.info("Done saving loanee data from file to db. loanees size {}", savedLoanees.size());
        return savedLoanees;
    }
    private List<String[]> readFile(File file) throws MeedlException {
        List<String[]> data;
        if (file.getName().endsWith(".csv")) {
            try {
                data = readCSV(file);
            }catch (IOException e){
                log.error("Error occurred reading csv",e);
                throw new MeedlException(e.getMessage());
            }
        } else if (file.getName().endsWith(".xlsx")) {
            try{
                data = readExcel(file);
            }catch (IOException e){
                log.error("Error occurred reading excel",e);
                throw new MeedlException(e.getMessage());
            }
        } else {
            log.error("Unsupported file type.");
            throw new MeedlException("Unsupported file type.");
        }
        return data;
    }

    private List<String[]> readCSV(File file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                rows.add(line.split(","));
            }
        }
        return rows;
    }

    private List<String[]> readExcel(File file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            boolean headerSkipped = false;
            for (Row row : sheet) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue; // skip header
                }
                String[] values = new String[3];
                values[0] = row.getCell(0).getStringCellValue();
                values[1] = String.valueOf((int) row.getCell(1).getNumericCellValue());
                values[2] = row.getCell(2).getStringCellValue();
                rows.add(values);
            }
        }
        return rows;
    }
}
