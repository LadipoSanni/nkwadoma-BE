package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.*;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook.AsynchronousLoanBookProcessingUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook.LoanUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook.RepaymentHistoryUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loancalculation.LoanCalculationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.aes.AesOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationLoanDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanReferralOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanDecision;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.ModeOfPayment;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.UploadedStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanRequestStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.education.ProgramLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.validation.LoanBookValidator;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoanException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class AsynchronousLoanBookProcessing implements AsynchronousLoanBookProcessingUseCase {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final LoaneeOutputPort loaneeOutputPort;
    private final LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    private final IdentityManagerOutputPort identityManagerOutputPort;
    private final CohortUseCase cohortUseCase;
    private final LoanBookValidator loanBookValidator;
    private final LoaneeUseCase loaneeUseCase;
    private final RespondToLoanReferralUseCase respondToLoanReferralUseCase;
    private final LoanRequestUseCase loanRequestUseCase;
    private final LoanOfferUseCase loanOfferUseCase;
    private final RepaymentHistoryUseCase repaymentHistoryUseCase;
    private final LoanProductOutputPort loanProductOutputPort;
    private final CohortOutputPort cohortOutputPort;
    private final CohortLoaneeOutputPort cohortLoaneeOutputPort;
    private final LoanReferralOutputPort loanReferralOutputPort;
    private final LoanCalculationUseCase loanCalculationUseCase;
    private final LoanUseCase loanUseCase;
    private final AesOutputPort aesOutputPort;
    private final CohortLoanDetailOutputPort cohortLoanDetailOutputPort;
    private final ProgramLoanDetailOutputPort programLoanDetailOutputPort;
    private final OrganizationLoanDetailOutputPort organizationLoanDetailOutputPort;

    @Override
    public void upLoadUserData(LoanBook loanBook) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanBook, "Loan book cannot be empty.");
        loanBook.validateLoanBook();

        List<String> requiredHeaders = getUserDataUploadHeaders();

        List<Map<String, String>> data = readFile(loanBook, requiredHeaders);
        loanBookValidator.validateUserDataUploadFile(loanBook, data, requiredHeaders);
        log.info("Loan book read is {}", data);

        Cohort savedCohort = findCohort(loanBook.getCohort());
        List<CohortLoanee> convertedCohortLoanees = convertToLoanees(data, savedCohort, loanBook.getActorId());
        convertedCohortLoanees = addUploadedLoaneeToCohort(convertedCohortLoanees);

        log.info("Converted loanees size {}", convertedCohortLoanees.size());
//        validateStartDates(convertedCohortLoanees, savedCohort);
        loanBook.setCohortLoanees(convertedCohortLoanees);
        referCohort(loanBook);
        completeLoanProcessing(loanBook);
        updateLoaneeCount(savedCohort,convertedCohortLoanees);
    }


    @Override
    public void uploadRepaymentHistory(LoanBook repaymentHistoryBook) throws MeedlException {
        MeedlValidator.validateObjectInstance(repaymentHistoryBook, "Repayment record book cannot be empty.");
        repaymentHistoryBook.validateRepaymentRecord();
        List<String> requiredHeaders = getRepaymentRecordUploadRequiredHeaders();

        List<Map<String, String>>  data = readFile(repaymentHistoryBook, requiredHeaders);
        repaymentHistoryBook.setMeedlNotification(new MeedlNotification());
        log.info("Repayment record book read is {}", data);


        loanBookValidator.repaymentHistoryValidation(data, repaymentHistoryBook);
        Cohort savedCohort = findCohort(repaymentHistoryBook.getCohort());
        repaymentHistoryBook.setCohort(savedCohort);
        List<RepaymentHistory> convertedRepaymentHistories = convertToRepaymentHistory(data);
        repaymentHistoryBook.setRepaymentHistories(convertedRepaymentHistories);

        Set<String> loaneesThatMadePayment = getSetOfLoanees(convertedRepaymentHistories);
        log.info("Set of loanees that made payments size : {}, set",loaneesThatMadePayment.size());
        Map<String, List<RepaymentHistory>> mapOfRepaymentHistoriesForEachLoanee = getRepaymentHistoriesForLoanees(loaneesThatMadePayment, convertedRepaymentHistories);
//        printRepaymentCountsPerLoanee(mapOfRepaymentHistoriesForEachLoanee);
        processRepaymentCalculation(mapOfRepaymentHistoriesForEachLoanee, repaymentHistoryBook.getCohort().getId(), repaymentHistoryBook);
        log.info("Repayment record uploaded..");
    }

    private void validateStartDates(List<Loanee> convertedLoanees, Cohort savedCohort) throws MeedlException {
        for (Loanee loanee : convertedLoanees) {
            validateStartDate(loanee.getUpdatedAt(), savedCohort.getStartDate());
        }

    }

    private void validateStartDate(LocalDateTime loanStartDate, LocalDate cohortStartDate) throws MeedlException {
        LocalDate loanStartAsDate = loanStartDate.toLocalDate();

        if (loanStartAsDate.isBefore(cohortStartDate)) {
            log.info("Loan start date {} cannot be before cohort start date {}.", loanStartAsDate, cohortStartDate);
            throw new LoanException("Loan start date " +loanStartAsDate +" cannot be before cohort start date "+cohortStartDate );
        }
    }

    private void updateLoaneeCount(Cohort savedCohort, List<CohortLoanee> loanees) throws MeedlException {
        savedCohort = findCohort(savedCohort);
        log.info("Number of loanees in a cohort on upload {}", savedCohort.getNumberOfLoanees() + loanees.size());
        savedCohort.setNumberOfLoanees(savedCohort.getNumberOfLoanees() + loanees.size());
        savedCohort.setNumberOfLoanRequest(savedCohort.getNumberOfLoanRequest() + loanees.size());
        savedCohort.setStillInTraining(savedCohort.getStillInTraining() + loanees.size());
        savedCohort.setNumberOfReferredLoanee(savedCohort.getNumberOfReferredLoanee() + loanees.size());
        log.info("Number of loanees in the cohort updated before save {}", savedCohort.getNumberOfLoanees());
        cohortOutputPort.save(savedCohort);
        loaneeUseCase.increaseNumberOfLoaneesInOrganization(savedCohort, loanees.size());
        loaneeUseCase.increaseNumberOfLoaneesInProgram(savedCohort, loanees.size());
    }
    public void printRepaymentCountsPerLoanee(Map<String, List<RepaymentHistory>> mapOfRepaymentHistoriesForEachLoanee) {
        for (Map.Entry<String, List<RepaymentHistory>> entry : mapOfRepaymentHistoriesForEachLoanee.entrySet()) {
            String loaneeId = entry.getKey();
            int numberOfRepayments = entry.getValue() != null ? entry.getValue().size() : 0;

            log.info("Loanee: {} | Repayments: {}", loaneeId, numberOfRepayments);
        }
    }

    public void processRepaymentCalculation(
            Map<String, List<RepaymentHistory>> mapOfRepaymentHistoriesForEachLoanee,
            String cohortId,
            LoanBook repaymentRecordBook) throws MeedlException {

        for (Map.Entry<String, List<RepaymentHistory>> entry : mapOfRepaymentHistoriesForEachLoanee.entrySet()) {
            String loaneeId = entry.getKey();
            List<RepaymentHistory> repaymentHistories = entry.getValue();
            BigDecimal totalAmountRepaid = loanCalculationUseCase.calculateCurrentTotalAmountRepaid(repaymentHistories, loaneeId, cohortId);
//            repaymentRecordBook.setRepaymentHistories(repaymentHistories);

            calculateLoaneeLoanDetails(cohortId, loaneeId, totalAmountRepaid);

        }
        List<RepaymentHistory> savedRepaymentHistories = repaymentHistoryUseCase.saveCohortRepaymentHistory(repaymentRecordBook);
        log.info("repayment histories for loanees {}", savedRepaymentHistories);
        log.info("Done processing accumulated repayments.");
    }

    private void calculateLoaneeLoanDetails(String cohortId, String loaneeId, BigDecimal totalAmountRepaid) throws MeedlException {

        BigDecimal currentAmountPaid = updateLoaneeLoanDetail(totalAmountRepaid,cohortId, loaneeId);

        CohortLoanDetail cohortLoanDetail = updateCohortLoanDetail(cohortId, currentAmountPaid);

        ProgramLoanDetail programLoanDetail = updateProgramLoanDetail(cohortLoanDetail, currentAmountPaid);

        OrganizationLoanDetail organizationLoanDetail = updateOrganizationLoanDetail(programLoanDetail, currentAmountPaid);
        log.info("Organization loan details after saving {}",organizationLoanDetail);

    }

    private OrganizationLoanDetail updateOrganizationLoanDetail(ProgramLoanDetail programLoanDetail, BigDecimal currentAmountPaid) throws MeedlException {
        log.info("About to Update Organization loan detail after repayment ");
        OrganizationLoanDetail organizationLoanDetail = organizationLoanDetailOutputPort.findByOrganizationId(
                programLoanDetail.getProgram().getOrganizationIdentity().getId());
        log.info("organization loan detail found {}", organizationLoanDetail);
        organizationLoanDetail.setTotalAmountRepaid(organizationLoanDetail.getTotalAmountRepaid().add(currentAmountPaid));
        organizationLoanDetail.setTotalOutstandingAmount(organizationLoanDetail.getTotalOutstandingAmount().subtract(currentAmountPaid));
        log.info("Updated Organization loan detail after repayment  {}", organizationLoanDetail);
        organizationLoanDetail = organizationLoanDetailOutputPort.save(organizationLoanDetail);
        return organizationLoanDetail;
    }

    private ProgramLoanDetail updateProgramLoanDetail(CohortLoanDetail cohortLoanDetail, BigDecimal currentAmountPaid) throws MeedlException {
        log.info("About to Update Program loan detail after repayment ");
        ProgramLoanDetail programLoanDetail = programLoanDetailOutputPort.findByProgramId(cohortLoanDetail.getCohort().getProgramId());
        log.info("program loan detail found {}", programLoanDetail);
        programLoanDetail.setTotalAmountRepaid(programLoanDetail.getTotalAmountRepaid().add(currentAmountPaid));
        programLoanDetail.setTotalOutstandingAmount(programLoanDetail.getTotalOutstandingAmount().subtract(currentAmountPaid));
        log.info("Updated Program loan detail after repayment  {}", programLoanDetail);
        programLoanDetail = programLoanDetailOutputPort.save(programLoanDetail);
        log.info("Program loan details after saving {}",programLoanDetail);
        return programLoanDetail;
    }

    private CohortLoanDetail updateCohortLoanDetail(String cohortId, BigDecimal currentAmountPaid) throws MeedlException {
        log.info("About to Update Cohort loan detail after repayment ");
        CohortLoanDetail cohortLoanDetail = cohortLoanDetailOutputPort.findByCohortId(cohortId);
        log.info("cohort loan detail found {}", cohortLoanDetail);
        cohortLoanDetail.setTotalAmountRepaid(cohortLoanDetail.getTotalAmountRepaid().add(currentAmountPaid));
        cohortLoanDetail.setTotalOutstandingAmount(cohortLoanDetail.getTotalOutstandingAmount().subtract(currentAmountPaid));
        log.info("Updated Cohort loan detail after repayment  {}", cohortLoanDetail);
        cohortLoanDetail = cohortLoanDetailOutputPort.save(cohortLoanDetail);
        log.info("cohort loan details after saving {}",cohortLoanDetail);
        return cohortLoanDetail;
    }

    private BigDecimal updateLoaneeLoanDetail(BigDecimal totalAmountRepaid, String cohortId, String loaneeId) throws MeedlException {
        CohortLoanee cohortLoanee = cohortLoaneeOutputPort.findCohortLoaneeByLoaneeIdAndCohortId(loaneeId, cohortId);
        log.info("cohort loanee found {}",cohortLoanee);
        LoaneeLoanDetail loaneeLoanDetail = loaneeLoanDetailsOutputPort.findByCohortLoaneeId(cohortLoanee.getId());

        BigDecimal currentAmountPaid = totalAmountRepaid.subtract(loaneeLoanDetail.getAmountRepaid());
        log.info("total amount repaid {}", totalAmountRepaid);
        log.info("Current amount paid {}",currentAmountPaid);

        log.info("loanee loan details {}",loaneeLoanDetail);
        loaneeLoanDetail.setAmountRepaid(loaneeLoanDetail.getAmountRepaid().add(currentAmountPaid));
        loaneeLoanDetail.setAmountOutstanding(loaneeLoanDetail.getAmountOutstanding().subtract(currentAmountPaid));
        log.info("loanee loan detail after setting repayment  {}", loaneeLoanDetail);
        LoaneeLoanDetail updatedLoaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
        log.info("Updated Loanee loan detail after repayment {}", updatedLoaneeLoanDetail);
        return currentAmountPaid;
    }

    public Map<String, List<RepaymentHistory>> getRepaymentHistoriesForLoanees(
            Set<String> loaneeEmails,
            List<RepaymentHistory> allRepayments
    ){
        Map<String, List<RepaymentHistory>> result = loaneeEmails.stream()
                .map(email -> {
                    try {
                        Loanee loanee = loaneeOutputPort.findByLoaneeEmail(email);
                        List<RepaymentHistory> repaymentHistories = getRepaymentsByEmail(allRepayments, email);
                        return Map.entry(loanee.getId(), repaymentHistories);
                    } catch (MeedlException e) {
                        log.error("Repayment processing for each loanee failed to find loanee with email {}",email, e);
                        throw new RuntimeException("Repayment processing. Failed to get loanee for email: " + email, e);
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        log.info("Repayment histories in map {}", result);
        return result;
    }

    @Override
    public List<RepaymentHistory> getRepaymentsByEmail(List<RepaymentHistory> allRepayments, String email) {
        return allRepayments.stream()
                .filter(rh -> {
                    Loanee loanee = rh.getLoanee();
                    return loanee != null &&
                            loanee.getUserIdentity() != null &&
                            email.equals(loanee.getUserIdentity().getEmail());
                })
                .collect(Collectors.toList());
    }

    private Set<String> getSetOfLoanees(List<RepaymentHistory> repaymentHistories) {
        Set<String> repayingUserEmails = repaymentHistories.stream()
                .map(RepaymentHistory::getLoanee)
                .filter(Objects::nonNull)
                .map(Loanee::getUserIdentity)
                .filter(Objects::nonNull)
                .map(UserIdentity::getEmail)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return repayingUserEmails;
    }

    private void completeLoanProcessing(LoanBook loanBook) {
        loanBook.getCohortLoanees()
                .forEach(cohortLoanee -> {
                    try {
                        log.info("Loanee with cohort name but is loan product name {}", cohortLoanee.getCohort().getName());
                        log.info("Loan start date before processing start loan before accepting loan referral {}", cohortLoanee.getReferralDateTime());

                        LoanReferral loanReferral = acceptLoanReferral(cohortLoanee);
                        log.info("loan referral is {}", loanReferral);
                        LoanRequest loanRequest = acceptLoanRequest(cohortLoanee, loanReferral, loanBook);
                        log.info("loan request is {}", loanRequest);
                        loanRequest.setLoanee(cohortLoanee.getLoanee());
                        acceptLoanOffer(loanRequest);
                        startLoan(loanRequest,cohortLoanee.getUpdatedAt() );
                    } catch (MeedlException e) {
                        log.error("Error accepting loan referral.",e);
                    }
                });
    }

    private void startLoan(LoanRequest loanRequest, LocalDateTime loanStartDate) throws MeedlException {
        log.info("loan request is {}", loanRequest);
        log.info("The loan start date is {} for user with email {}", loanStartDate, loanRequest.getLoanee().getUserIdentity().getEmail());
        Loan loan = Loan.builder().loaneeId(loanRequest.getLoanee().getId()).startDate(loanStartDate).loanOfferId(loanRequest.getId()).build();
        loanUseCase.startLoan(loan);
        log.info("Loan started for loanee {}", loanRequest.getLoanee().getUserIdentity().getEmail());
    }

    private void acceptLoanOffer(LoanRequest loanRequest) throws MeedlException {
        LoanOffer loanOffer = new LoanOffer();
        loanOffer.setId(loanRequest.getId());
        loanOffer.setLoaneeResponse(LoanDecision.ACCEPTED);
        loanOffer.setUserId(loanRequest.getLoanee().getUserIdentity().getId());
        loanOfferUseCase.acceptLoanOffer(loanOffer);
    }

    private LoanReferral acceptLoanReferral(CohortLoanee cohortLoanee) throws MeedlException {

        LoanReferral loanReferral = loanReferralOutputPort.findLoanReferralByCohortLoaneeId(cohortLoanee.getId());
        loanReferral.setLoanReferralStatus(LoanReferralStatus.ACCEPTED);
        respondToLoanReferralUseCase.respondToLoanReferral(loanReferral);
        return loanReferral;
    }
    private LoanRequest acceptLoanRequest(CohortLoanee loanee, LoanReferral loanReferral, LoanBook loanBook) throws MeedlException {
        log.info("Loan Amount Approved is {}", loanee.getLoaneeLoanDetail().getAmountApproved());
        log.info("Amount received by this loanee {}", loanee.getLoaneeLoanDetail().getAmountReceived());
        LoanRequest loanRequest = LoanRequest.builder()
                .loanAmountApproved(loanee.getLoaneeLoanDetail().getAmountReceived())
                .loanAmountRequested(loanee.getLoaneeLoanDetail().getAmountRequested())
                .loanRequestDecision(LoanDecision.ACCEPTED)
                .id(loanReferral.getId())
                .loanProductId(findLoanProductIdByName(loanee.getLoanee().getLoanProductName()))
                .actorId(loanBook.getActorId())
                .referredBy(loanee.getReferredBy())
                .createdDate(LocalDateTime.now())
                .dateTimeApproved(LocalDateTime.now())
                .status(LoanRequestStatus.APPROVED)
                .build();
        log.info("Accepting loan request for uploaded loanee {}", loanRequest);
        return loanRequestUseCase.respondToLoanRequest(loanRequest);
    }

    private String findLoanProductIdByName(String loanProductName) {
        LoanProduct loanProduct = null;
        try {
            log.info("Loan product name being searched for in upload user data {}", loanProductName);
            loanProduct = loanProductOutputPort.findByName(loanProductName);
        } catch (MeedlException e) {
            log.error("Loan product does not exist by this name {}", loanProductName);
            throw new RuntimeException(e);
        }

        return loanProduct.getId();
    }


    private void referCohort(LoanBook loanBook) {
        Iterator<CohortLoanee> iterator = loanBook.getCohortLoanees().iterator();
        while (iterator.hasNext()) {
            CohortLoanee cohortLoanee = iterator.next();
            log.info("About to refer loanee with details {}", cohortLoanee);
            log.info("Loan product name from cohort loanee at this point is  {}", cohortLoanee.getLoanee().getLoanProductName());
            log.info("About to refer loanee in cohort with loan details {}", cohortLoanee.getLoaneeLoanDetail());
            try {
                inviteTrainee(cohortLoanee);
            } catch (MeedlException e) {
                log.error("Failed to invite trainee with id: {}", cohortLoanee.getId(), e);
                iterator.remove();
            }
        }

        log.info("Number of referable loanees :{} ",  loanBook.getCohortLoanees().size());
//        asynchronousMailingOutputPort.notifyLoanReferralActors(loanBook.getLoanees());
    }
    private void inviteTrainee (CohortLoanee loanee) throws MeedlException {
        log.info("Single loanee is being referred...");
        loaneeUseCase.referLoanee(loanee);
    }

    private Cohort findCohort(Cohort cohort) throws MeedlException {
        MeedlValidator.validateObjectInstance(cohort, CohortMessages.COHORT_CANNOT_BE_EMPTY.getMessage());
        return cohortUseCase.viewCohortDetails(cohort.getCreatedBy(), cohort.getId());
    }

    private List<RepaymentHistory> convertToRepaymentHistory(List<Map<String, String>>  data) throws MeedlException {
        List<RepaymentHistory> repaymentHistories = new ArrayList<>();

        log.info("Started creating Repayment record from data gotten from file upload {}, size {}",data, data.size());
        for (Map<String, String> row  : data) {

            RepaymentHistory repaymentHistory = RepaymentHistory.builder()
                    .loanee(Loanee.builder().userIdentity(UserIdentity.builder().email(validateUseEmail(row.get("email").trim())).build()).build())
                    .amountPaid(validateMoney(row.get("amountpaid").trim(), "Amount repaid should be properly indicated"))
                    .paymentDateTime(parseFlexibleDateTime(row.get("paymentdate").trim(), row.get("email")))
//                    .modeOfPayment(validateModeOfPayment(row.get("modeofpayment").trim()))
                    .modeOfPayment(ModeOfPayment.TRANSFER)
                    .build();
            log.info("Repayment history model created from file {}", repaymentHistory);
            repaymentHistories.add(repaymentHistory);
        }
        return repaymentHistories;
    }

    private String validateUseEmail(String email) {
        try{
            MeedlValidator.validateEmail(email);
        }catch (MeedlException e){

        }
        return email;
    }


    private LocalDateTime parseFlexibleDateTime(String dateStr, String email) throws MeedlException {
        log.info("Repayment date before formating {}", dateStr);
        if (dateStr == null || MeedlValidator.isEmptyString(dateStr)) {
            return null;
        }

        dateStr = dateStr.trim().replace("/", "-");
        log.info("Repayment date after formating {}", dateStr);
        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("yyyy-M-d"),
                DateTimeFormatter.ofPattern("d-M-yyyy"),
                DateTimeFormatter.ofPattern("yyyy-M-d")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                log.info("The formatter is {} for {}", formatter, dateStr);
                if (formatter == DateTimeFormatter.ISO_LOCAL_DATE_TIME) {
                    log.info("In ISO_LOCAL_DATE_TIME {}",dateStr);
                    return LocalDateTime.parse(dateStr, formatter);
                } else {
                    return LocalDate.parse(dateStr, formatter).atStartOfDay();
                }
            } catch (DateTimeParseException ignored) {
                log.error("Error occurred while converting the format.");
//                return LocalDate.parse(dateStr, formatter).atStartOfDay();
            }
        }

        log.error("The date format was invalid: {}", dateStr);
        throw new LoanException("Date doesn't match format dd/mm/yyyy. Date entered: "+dateStr+". For user "+email);
    }

    private ModeOfPayment validateModeOfPayment(String modeOfRepaymentToConvert) {
        if (MeedlValidator.isEmptyString(modeOfRepaymentToConvert)) {
            log.error("Mode of repayment as a string to be converted is empty {}", modeOfRepaymentToConvert);
            //Todo create notification of this error
        }
        ModeOfPayment modeOfPayment = null;
        try {
            modeOfPayment = ModeOfPayment.valueOf(modeOfRepaymentToConvert);
        } catch (Exception e) {
            log.error("Error converting mode of repayment from string to enum.", e);
            //Todo create notification on error
        }
        return modeOfPayment;
    }

    private BigDecimal validateMoney(String amountTobeConverted, String message) {
        BigDecimal amount = null;
        try {
            amount = new BigDecimal(amountTobeConverted);
            MeedlValidator.validateBigDecimalDataElement(amount);
        } catch (Exception e) {
            log.error("An error occurred while converting string to money {} ", message,e);
            //TODO notification should be made here
        }
        return amount;
    }

    List<CohortLoanee> convertToLoanees(List<Map<String, String>> data, Cohort cohort, String actorId) throws MeedlException {
        List<CohortLoanee> cohortLoanees = new ArrayList<>();
        for (Map<String, String> row : data) {
            UserIdentity userIdentity = UserIdentity.builder()
                    .firstName(row.get("firstname"))
                    .lastName(row.get("lastname"))
                    .middleName(row.get("middlename"))
                    .email(row.get("email"))
                    .phoneNumber(row.get("phonenumber"))
                    .role(IdentityRole.LOANEE)
                    .createdAt(LocalDateTime.now())
                    .bvn(encryptValue(row.get("bvn"), "Invalid bvn "))
                    .nin(encryptValue(row.get("nin"), "Invalid nin "))
                    .createdBy(actorId)
                    .build();
            LoaneeLoanDetail loaneeLoanDetail = LoaneeLoanDetail.builder()
                    .initialDeposit(new BigDecimal(row.get("initialdeposit")))
                    .amountRequested(new BigDecimal(row.get("amountrequested")))
                    .amountReceived(new BigDecimal(row.get("amountreceived")))
                    .amountOutstanding(new BigDecimal(row.get("amountreceived")).subtract(new BigDecimal(row.get("initialdeposit"))))
                    .amountRepaid(BigDecimal.ZERO)
                    .tuitionAmount(cohort.getTuitionAmount())
                    .build();
            log.info("loan product name found from csv {}", row.get("loanproduct"));
            Loanee loanee = Loanee.builder()
                    .userIdentity(userIdentity)
                    .loaneeLoanDetail(loaneeLoanDetail)
                    .loaneeStatus(LoaneeStatus.ADDED)
                    .onboardingMode(OnboardingMode.FILE_UPLOADED_FOR_DISBURSED_LOANS)
                    .uploadedStatus(UploadedStatus.ADDED)
                    .cohortId(cohort.getId())
                    .loanProductName(row.get("loanproduct"))
                    .updatedAt(parseFlexibleDateTime(row.get("loanstartdate"), row.get("email")))
                    .build();

            CohortLoanee cohortLoanee = CohortLoanee.builder()
                    .loaneeLoanDetail(loaneeLoanDetail)
                    .loaneeStatus(LoaneeStatus.ADDED)
                    .loanee(loanee)
                    .cohort(cohort)
                    .createdBy(cohort.getCreatedBy())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(parseFlexibleDateTime(row.get("loanstartdate"), row.get("email")))
                    .build();

            cohortLoanees.add(cohortLoanee);
        }
        log.info("Validating the file field values.");
        return cohortLoanees;
    }
    public String encryptValue(String value, String errorMessage) {
        try {
            MeedlValidator.validateElevenDigits(value, errorMessage);
            return aesOutputPort.encryptAES(value.trim());
        } catch (MeedlException e) {
            log.error("Unable to encrypt value {}", value);
        }
        return StringUtils.EMPTY;
    }


    private List<CohortLoanee> addUploadedLoaneeToCohort(List<CohortLoanee> cohortLoanees){
        List<CohortLoanee> savedLoanees = new ArrayList<>();
        log.info("Started saving converted data of cohortLoanees");
        for (CohortLoanee cohortLoanee : cohortLoanees){
            try {
                saveUploadedUserIdentity(cohortLoanee);
                LoaneeLoanDetail savedLoaneeLoanDetail = loaneeLoanDetailsOutputPort.save(cohortLoanee.getLoaneeLoanDetail());
                cohortLoanee.setLoaneeLoanDetail(savedLoaneeLoanDetail);
                log.info("Loanee's loan details after saving in file upload {}", savedLoaneeLoanDetail);
                cohortLoanee.setLoaneeLoanDetail(savedLoaneeLoanDetail);

                Loanee savedLoanee = getSavedLoanee(cohortLoanee);

                log.info("saved loanee in upload process == {} ",savedLoanee);
                savedLoanee.setLoanProductName(cohortLoanee.getLoanee().getLoanProductName());
                cohortLoanee.setLoanee(savedLoanee);
                log.info("Loanee's actual loan details in file upload: {}", cohortLoanee.getLoaneeLoanDetail());
                CohortLoanee savedCohortLoanee = cohortLoaneeOutputPort.save(cohortLoanee);
                savedCohortLoanee.getLoanee().setLoanProductName(cohortLoanee.getLoanee().getLoanProductName());
                log.info("Saved cohort loanee in upload, number of loanees in cohort {}", savedCohortLoanee.getCohort().getNumberOfLoanees());
                log.info("none saved cohort loanee in upload, number of loanees in cohort {}", cohortLoanee.getCohort().getNumberOfLoanees());
                log.info("The loan product name after saving the cohort loanee is {}", savedCohortLoanee.getLoanee().getLoanProductName());
                savedLoanees.add(savedCohortLoanee);
            } catch (MeedlException e) {
                log.info("Error occurred while saving uploaded loanee data ...", e);
            }
        }
        log.info("Done saving loanee data from file to db. cohortLoanees size {}", savedLoanees.size());
        return savedLoanees;
    }

    private Loanee getSavedLoanee(CohortLoanee cohortLoanee) throws MeedlException {
        Optional<Loanee> optionalLoaneeFound = loaneeOutputPort.findByUserId(cohortLoanee.getLoanee().getUserIdentity().getId());
        if (optionalLoaneeFound.isPresent()){
            log.info("Loanee been uploaded exist previously as a loanee ");
            cohortLoanee.getLoanee().setId(optionalLoaneeFound.get().getId());
            return cohortLoanee.getLoanee();
        }
        return loaneeOutputPort.save(cohortLoanee.getLoanee());
    }

    private void saveUploadedUserIdentity(CohortLoanee cohortLoanee) {
        UserIdentity identity = cohortLoanee.getLoanee().getUserIdentity();
        String email = identity.getEmail();

        try {
            UserIdentity createdIdentity = identityManagerOutputPort.createUser(identity);
            userIdentityOutputPort.save(createdIdentity);
            return;
        } catch (MeedlException e) {
            log.warn("Loanee already exists on platform for email: {}", email, e);
        }

        if (findUserInDbAndSetId(identity, email)) return;

        if (findUserInKeycloakAndSetId(identity, email)) return;

        log.error("Loanee wasn't found anywhere (DB or Keycloak) for email: {}", email);
    }

    private boolean findUserInDbAndSetId(UserIdentity identity, String email) {
        try {
            UserIdentity foundUser = userIdentityOutputPort.findByEmail(email);
            identity.setId(foundUser.getId());
            log.warn("User found in DB successfully for upload data. email: {}", email);
            return true;
        } catch (MeedlException e) {
            log.error("Unable to find user on bd by email on the platform in upload data flow after being unable to save user with email :{}", email);
            return false;
        }
    }
    private boolean findUserInKeycloakAndSetId(UserIdentity userIdentity, String email) {
        try {
            Optional<UserIdentity> optionalUser = identityManagerOutputPort.getUserByEmail(email);
            optionalUser.ifPresent(user -> userIdentity.setId(user.getId()));
            log.info("User identity exists on keycloak but not on db email : {}", email);
            userIdentityOutputPort.save(userIdentity);
            return optionalUser.isPresent();
        } catch (MeedlException e) {
            log.error("Loanee wasn't found on keycloak either in upload user data flow");
            return false;
        }
    }


    private List<Map<String, String>> readFile(LoanBook loanBook, List<String> requiredHeaders) throws MeedlException {
        List<Map<String, String>> data;
        if (loanBook.getFile().getName().endsWith(".csv")) {
            log.info("the file type is .csv");
            try {
                data = validateAndReadCSV(loanBook, requiredHeaders);
            }catch (IOException e){
                log.error("Error occurred reading csv",e);
                throw new MeedlException(e.getMessage());
            }
        } else if (loanBook.getFile().getName().endsWith(".xlsx")) {
            try{
                log.info("the file is a .xlsx file");
                data = validateAndReadExcel(loanBook.getFile());
            }catch (IOException e){
                log.error("Error occurred reading excel",e);
                throw new LoanException(e.getMessage());
            }
        } else {
            log.error("Unsupported file type.");
            throw new LoanException("Unsupported file type.");
        }
        return data;
    }

    private List<Map<String, String>> validateAndReadCSV(LoanBook loanBook, List<String> requiredHeaders) throws IOException, MeedlException {
        List<Map<String, String>> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(loanBook.getFile()))) {
            String headerLine = br.readLine();

            Map<String, Integer> headerIndexMap = getAndValidateFileHeaderMap(requiredHeaders, headerLine);

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] values = line.split(",");
                log.info("The row line to get has {}", Arrays.toString(values));
                Map<String, String> rowMap = new HashMap<>();

                for (String header : requiredHeaders) {
                    log.info("The header to get its value : {}", header);
                    if (!headerIndexMap.containsKey(header)) {
                        log.warn("Skipping missing header: {}", header);
                        continue;
                    }
                    int index = headerIndexMap.get(header);
                    if (index >= values.length) {
                        log.error("Missing value for column: {}", header);
                        throw new LoanException("Missing value for column: " + header);
                    }
                    rowMap.put(header, values[index].trim());
                }
                log.info("The row map is :{}", rowMap);

                records.add(rowMap);
                log.info("The records with row map is : {}", records);
            }
        }

        if (records.isEmpty()) {
            throw new LoanException("CSV file has no data rows.");
        }

        return records;
    }

    private static Map<String, Integer> getAndValidateFileHeaderMap(List<String> requiredHeaders, String headerLine) throws MeedlException {
        if (headerLine == null) {
            log.info("CSV file is empty or missing headers.");
            throw new LoanException("CSV file is empty or missing headers.");
        }

        log.info("Header line first read {}", headerLine);
        String[] headers = headerLine.split(",");
        log.info("Headers splited into a list {}", Arrays.toString(headers));
        Map<String, Integer> headerIndexMap = new HashMap<>();

        extractFileHeaderMap(headers, headerIndexMap);

        validateFileHeader(requiredHeaders, headerIndexMap);
        return headerIndexMap;
    }

    private static void extractFileHeaderMap(String[] headers, Map<String, Integer> headerIndexMap) {
        for (int i = 0; i < headers.length; i++) {
            log.info("Header in loop each value : {}", headers[i]);
            String formattedFileHeader = formatFileHeader(headers[i].trim());
            headerIndexMap.put(formattedFileHeader, i);
        }
    }

    private static String formatFileHeader(String header) {
        if (header == null) {
            return null;
        }
        return header.replaceAll("\\s+", "").toLowerCase();
    }

    private static void validateFileHeader(List<String> requiredHeaders, Map<String, Integer> headerIndexMap) throws MeedlException {
        log.info("Validation file headers with the required headers which are : {}", requiredHeaders);
        for (String required : requiredHeaders) {
            if (required.equals("bvn") || required.equals("nin")
                    || required.equals("modeofpayment") || required.equals("middlename")){
                continue;
            }
            if (!headerIndexMap.containsKey(required)) {
                log.error("Missing required column {}, Provided headers are {}", required, headerIndexMap);
                throw new LoanException("Missing required column: " + required);
            }
        }
    }


    private List<Map<String, String>> validateAndReadExcel(File file) throws IOException, MeedlException {
//        List<Map<String, String>> records = new ArrayList<>();
        throw new MeedlException("Please convert file to csv ");
//        List<String> requiredHeaders = List.of("firstName", "lastName", "email", "phoneNumber", "DON", "initialDeposit", "amountRequested", "amountReceived");
//
//        try (FileInputStream fis = new FileInputStream(file);
//             Workbook workbook = new XSSFWorkbook(fis)) {
//
//            Sheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rowIterator = sheet.iterator();
//
//            if (!rowIterator.hasNext()) {
//                throw new MeedlException("Excel file is empty.");
//            }
//
//            // Read header row
//            Row headerRow = rowIterator.next();
//            Map<String, Integer> headerIndexMap = new HashMap<>();
//
//            for (Cell cell : headerRow) {
//                String header = cell.getStringCellValue().trim();
//                headerIndexMap.put(header, cell.getColumnIndex());
//            }

//            validateFileHeader(requiredHeaders, headerIndexMap);

            // Read and map data rows
//            while (rowIterator.hasNext()) {
//                Row row = rowIterator.next();
//                if (isRowEmpty(row)) continue;
//
//                Map<String, String> rowMap = new HashMap<>();
//                for (String header : requiredHeaders) {
//                    log.info("Header for excel to get the actual cell : {}", header);
//                    int colIndex = headerIndexMap.get(header);
//                    Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
//                    if (cell == null) {
//                        throw new MeedlException("Missing value for column: " + header);
//                    }
//                    rowMap.put(header, getCellValueAsString(cell));
//                }
//
//                records.add(rowMap);
//            }
//        }

//        if (records.isEmpty()) {
//            throw new MeedlException("Excel file has no data rows.");
//        }

//        return records;
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
    private List<String> getUserDataUploadHeaders() {
        return List.of("firstname", "lastname", "middlename",
                "email", "phonenumber", "initialdeposit",
                "loanstartdate",
                "amountrequested", "amountreceived",
                "bvn", "nin", "loanproduct");
    }
    private List<String> getRepaymentRecordUploadRequiredHeaders() {
        return List.of(
                "email", "paymentdate",
                "amountpaid");
    }

}
