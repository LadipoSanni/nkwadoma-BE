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
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import javax.management.Notification;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

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
    public LoanBook upLoadUserData(LoanBook loanBook) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanBook, "Loan book cannot be empty.");
        loanBook.validateLoanBook();

        List<String> requiredHeaders = List.of("firstName", "lastName", "email", "phoneNumber", "DON", "initialDeposit", "amountRequested", "amountReceived");
        List<Map<String, String>> data = readFile(loanBook.getFile(), requiredHeaders);
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
        repaymentRecordBook.validateRepaymentRecord();
        List<String> requiredHeaders = List.of("firstName", "lastName", "email", "paymentDate", "amountPaid", "modeOfPayment");

        List<Map<String, String>>  data = readFile(repaymentRecordBook.getFile(), requiredHeaders);
        repaymentRecordBook.setMeedlNotification(new MeedlNotification());
//        List<String[]> data = readFile(repaymentRecordBook.getFile());
        log.info("Repayment record book read is {}", data);


        Cohort savedCohort = findCohort(repaymentRecordBook.getCohort());
        repaymentRecordBook.setCohort(savedCohort);
//        List<RepaymentHistory> convertedRepaymentHistories = convertToRepaymentHistory(data);
//        repaymentRecordBook.setRepaymentHistories(convertedRepaymentHistories);
        List<RepaymentHistory> savedRepaymentHistories = repaymentHistoryUseCase.saveCohortRepaymentHistory(repaymentRecordBook);
        log.info("Repayment record uploaded..");
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
//    private List<RepaymentHistory> convertToRepaymentHistory(List<Map<String, String>>  data) {
//        List<RepaymentHistory> repaymentHistories = new ArrayList<>();
//
//        log.info("Started creating Repayment record from data gotten from file upload {}, size {}",data, data.size());
//        for (String[] row : data) {
//            RepaymentHistory repaymentHistory = RepaymentHistory.builder()
//                    .firstName(row[0].trim())
//                    .lastName(row[1].trim())
//                    .userIdentity(UserIdentity.builder().email(row[2].trim()).build())
//                    .paymentDate(row[3].trim())
//                    .amountPaid(new BigDecimal(row[4].trim()))
//                    .modeOfPayment(ModeOfPayment.valueOf(row[5].trim()))
//                    .build();
//            log.info("Repayment history model created from file {}", repaymentHistory);
//            repaymentHistories.add(repaymentHistory);
//        }
//        return repaymentHistories;
//    }


    List<Loanee> convertToLoanees(List<Map<String, String>> data, Cohort cohort) {
        List<Loanee> loanees = new ArrayList<>();

        for (Map<String, String> row : data) {
            UserIdentity userIdentity = UserIdentity.builder()
                    .firstName(row.get("firstName"))
                    .lastName(row.get("lastName"))
                    .email(row.get("email"))
                    .phoneNumber(row.get("phoneNumber"))
                    .dateOfBirth(row.get("DON"))
                    .role(IdentityRole.LOANEE)
                    .createdAt(LocalDateTime.now())
                    .createdBy("73de0343-be48-4967-99ea-10be007e4347")
                    .build();

            LoaneeLoanDetail loaneeLoanDetail = LoaneeLoanDetail.builder()
                    .initialDeposit(new BigDecimal(row.get("initialDeposit")))
                    .amountRequested(new BigDecimal(row.get("amountRequested")))
                    .amountReceived(new BigDecimal(row.get("amountReceived")))
                    .build();

            Loanee loanee = Loanee.builder()
                    .userIdentity(userIdentity)
                    .loaneeLoanDetail(loaneeLoanDetail)
                    .loaneeStatus(LoaneeStatus.ADDED)
                    .onboardingMode(OnboardingMode.FILE_UPLOADED_FOR_DISBURSED_LOANS)
                    .cohortId(cohort.getId())
                    .build();

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
    private List<Map<String, String>> readFile(File file, List<String> requiredHeaders) throws MeedlException {
        List<Map<String, String>> data;
        if (file.getName().endsWith(".csv")) {
            try {
                data = validateAndReadCSV(file, requiredHeaders);
            }catch (IOException e){
                log.error("Error occurred reading csv",e);
                throw new MeedlException(e.getMessage());
            }
        } else if (file.getName().endsWith(".xlsx")) {
            try{
                data = validateAndReadExcel(file);
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

    private List<Map<String, String>> validateAndReadCSV(File file, List<String> requiredHeaders) throws IOException {
        List<Map<String, String>> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String headerLine = br.readLine();

            Map<String, Integer> headerIndexMap = getStringIntegerMap(requiredHeaders, headerLine);

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] values = line.split(",");
                Map<String, String> rowMap = new HashMap<>();

                for (String header : requiredHeaders) {
                    int index = headerIndexMap.get(header);
                    if (index >= values.length) {
                        throw new IllegalArgumentException("Missing value for column: " + header);
                    }
                    rowMap.put(header, values[index].trim());
                }

                records.add(rowMap);
            }
        }

        if (records.isEmpty()) {
            throw new IllegalArgumentException("CSV file has no data rows.");
        }

        return records;
    }

    private static Map<String, Integer> getStringIntegerMap(List<String> requiredHeaders, String headerLine) {
        if (headerLine == null) {
            throw new IllegalArgumentException("CSV file is empty or missing headers.");
        }

        String[] headers = headerLine.split(",");
        Map<String, Integer> headerIndexMap = new HashMap<>();

        for (int i = 0; i < headers.length; i++) {
            headerIndexMap.put(headers[i].trim(), i);
        }

        // Check for missing headers
        for (String required : requiredHeaders) {
            if (!headerIndexMap.containsKey(required)) {
                throw new IllegalArgumentException("Missing required column: " + required);
            }
        }
        return headerIndexMap;
    }


    private List<Map<String, String>> validateAndReadExcel(File file) throws IOException {
        List<Map<String, String>> records = new ArrayList<>();
        List<String> requiredHeaders = List.of("firstName", "lastName", "email", "phoneNumber", "DON", "initialDeposit", "amountRequested", "amountReceived");

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) {
                throw new IllegalArgumentException("Excel file is empty.");
            }

            // Read header row
            Row headerRow = rowIterator.next();
            Map<String, Integer> headerIndexMap = new HashMap<>();

            for (Cell cell : headerRow) {
                String header = cell.getStringCellValue().trim();
                headerIndexMap.put(header, cell.getColumnIndex());
            }

            // Validate required headers
            for (String required : requiredHeaders) {
                if (!headerIndexMap.containsKey(required)) {
                    throw new IllegalArgumentException("Missing required column: " + required);
                }
            }

            // Read and map data rows
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (isRowEmpty(row)) continue;

                Map<String, String> rowMap = new HashMap<>();
                for (String header : requiredHeaders) {
                    int colIndex = headerIndexMap.get(header);
                    Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (cell == null) {
                        throw new IllegalArgumentException("Missing value for column: " + header);
                    }
                    rowMap.put(header, getCellValueAsString(cell));
                }

                records.add(rowMap);
            }
        }

        if (records.isEmpty()) {
            throw new IllegalArgumentException("Excel file has no data rows.");
        }

        return records;
    }
    private boolean isRowEmpty(Row row) {
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private String getCellValueAsString(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue()).replaceAll("\\.0$", "");
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

}
