package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.*;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook.LoanBookUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook.LoanUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loancalculation.CalculationEngineUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.aes.AesOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanProduct.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanReferralOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanAggregateOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.EmploymentStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlConstants;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.UploadType;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanDecision;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.ModeOfPayment;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.UploadedStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanRequestStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.CalculationContext;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.validation.LoanBookValidator;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoanException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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
public class LoanBookService implements LoanBookUseCase {
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
    private final LoanProductOutputPort loanProductOutputPort;
    private final CohortOutputPort cohortOutputPort;
    private final CohortLoaneeOutputPort cohortLoaneeOutputPort;
    private final LoanReferralOutputPort loanReferralOutputPort;
    private final CalculationEngineUseCase loanCalculationUseCase;
    private final AsynchronousNotificationOutputPort asynchronousNotificationOutputPort;
    private final LoanUseCase loanUseCase;
    private final AesOutputPort aesOutputPort;
    private final LoaneeLoanAggregateOutputPort loaneeLoanAggregateOutputPort;
    private final PortfolioOutputPort portfolioOutputPort;

    @Override
    public void upLoadUserData(LoanBook loanBook) throws MeedlException {
        loanBookValidator.validateLoanBookObjectValues(loanBook);
        List<String> requiredHeaders = getUserDataUploadHeaders();

        loanBook.setUploadType(UploadType.USER_DATA);
        loanBook.setRequiredHeaders(requiredHeaders);

        List<Map<String, String>> data = readFile(loanBook);
        loanBookValidator.validateUserDataUploadFile(loanBook, data, requiredHeaders);
        log.info("Loan book read is {}", data);

        Cohort savedCohort = findCohort(loanBook.getCohort());
        List<CohortLoanee> convertedCohortLoanees = convertToLoanees(data, savedCohort, loanBook.getActorId());
        convertedCohortLoanees = addUploadedLoaneeToCohort(convertedCohortLoanees);

        log.info("Converted loanees size {}", convertedCohortLoanees.size());
        loanBook.setCohortLoanees(convertedCohortLoanees);
        referCohort(loanBook);
        completeLoanProcessing(loanBook);
        updateLoaneeCount(savedCohort, convertedCohortLoanees);
        sendUserDataUploadSuccessNotification(loanBook);
        log.info("Upload of user data done!");
    }

    @Override
    public void uploadRepaymentHistory(LoanBook repaymentHistoryBook) throws MeedlException {
        loanBookValidator.validateLoanBookObjectValues(repaymentHistoryBook);
        List<String> requiredHeaders = getRepaymentRecordUploadRequiredHeaders();

        repaymentHistoryBook.setUploadType(UploadType.REPAYMENT);
        repaymentHistoryBook.setRequiredHeaders(requiredHeaders);

        List<Map<String, String>>  data = readFile(repaymentHistoryBook);
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
        processRepaymentCalculation(mapOfRepaymentHistoriesForEachLoanee, repaymentHistoryBook.getCohort());
        sendRepaymentUploadSuccessNotification(repaymentHistoryBook);

        log.info("Repayment record uploaded..");
    }
    private void sendUserDataUploadSuccessNotification(LoanBook loanBook) throws MeedlException {
        UserIdentity foundActor = identityManagerOutputPort.getUserById(loanBook.getActorId());
        asynchronousNotificationOutputPort.notifyPmOnUserDataUploadSuccess(foundActor, loanBook);
    }
    private void sendRepaymentUploadSuccessNotification(LoanBook loanBook) throws MeedlException {
        UserIdentity foundActor = identityManagerOutputPort.getUserById(loanBook.getActorId());
        asynchronousNotificationOutputPort.notifyPmOnRepaymentUploadSuccess(foundActor, loanBook);
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

    public void processRepaymentCalculation(
            Map<String, List<RepaymentHistory>> mapOfRepaymentHistoriesForEachLoanee,
            Cohort cohort) throws MeedlException {

        for (Map.Entry<String, List<RepaymentHistory>> entry : mapOfRepaymentHistoriesForEachLoanee.entrySet()) {
            String loaneeId = entry.getKey();
            List<RepaymentHistory> repaymentHistories = entry.getValue();
            Loanee loanee = loaneeOutputPort.findLoaneeById(loaneeId);
            if(ObjectUtils.isNotEmpty(loanee)){
                CalculationContext calculationContext = CalculationContext.builder()
                        .cohort(cohort)
                        .loanee(loanee)
                        .repaymentHistories(repaymentHistories).build();
                loanCalculationUseCase.calculateLoaneeLoanRepaymentHistory(calculationContext);
            }
        }
        log.info("Done processing accumulated repayments.");
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
        return repaymentHistories.stream()
                            .map(RepaymentHistory::getLoanee)
                            .filter(Objects::nonNull)
                            .map(Loanee::getUserIdentity)
                            .filter(Objects::nonNull)
                            .map(UserIdentity::getEmail)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());
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
                        startLoan(loanRequest,cohortLoanee.getLoaneeLoanDetail().getLoanStartDate());
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
        loanOfferUseCase.acceptLoanOffer(loanOffer, OnboardingMode.FILE_UPLOADED_FOR_DISBURSED_LOANS);
    }

    private LoanReferral acceptLoanReferral(CohortLoanee cohortLoanee) throws MeedlException {

        LoanReferral loanReferral = loanReferralOutputPort.findLoanReferralByCohortLoaneeId(cohortLoanee.getId());
        loanReferral.setLoanReferralStatus(LoanReferralStatus.ACCEPTED);
        loanReferral.setLoaneeUserId(cohortLoanee.getLoanee().getUserIdentity().getId());
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
            amount = loanBookValidator.parseNumericStringToBigDecimal(amountTobeConverted);
        } catch (Exception e) {
            log.error("An error occurred while converting string to money {} ", message,e);
            //TODO notification should be made here
        }
        return amount;
    }

    List<CohortLoanee> convertToLoanees(List<Map<String, String>> data, Cohort cohort, String actorId) throws MeedlException {
        List<CohortLoanee> cohortLoanees = new ArrayList<>();
        for (Map<String, String> row : data) {
            log.info("Bvn {} and nin {} for each loanee", row.get("bvn"), row.get("nin"));
            LocalDateTime loanStartDate = parseFlexibleDateTime(row.get("loanstartdate"), row.get("email"));
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
                    .initialDeposit(loanBookValidator.parseNumericStringToBigDecimal(row.get("initialdeposit")))
                    .amountRequested(loanBookValidator.parseNumericStringToBigDecimal(row.get("amountrequested")))
                    .amountReceived(loanBookValidator.parseNumericStringToBigDecimal(row.get("amountreceived")))
                    .amountOutstanding(loanBookValidator.parseNumericStringToBigDecimal(row.get("amountreceived")).subtract(new BigDecimal(row.get("initialdeposit"))))
                    .amountRepaid(BigDecimal.ZERO)
                    .interestIncurred(BigDecimal.ZERO)
                    .tuitionAmount(cohort.getTuitionAmount())
                    .loanStartDate(loanStartDate)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            log.info("loan product name found from csv {}", row.get("loanproduct"));
            Loanee loanee = Loanee.builder()
                    .userIdentity(userIdentity)
                    .loaneeLoanDetail(loaneeLoanDetail)
                    .loaneeStatus(LoaneeStatus.ADDED)
                    .onboardingMode(OnboardingMode.FILE_UPLOADED_FOR_DISBURSED_LOANS)
                    .uploadedStatus(UploadedStatus.ADDED)
                    .activationStatus(ActivationStatus.PENDING_INVITE)
                    .cohortId(cohort.getId())
                    .loanProductName(row.get("loanproduct"))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            CohortLoanee cohortLoanee = CohortLoanee.builder()
                    .loaneeLoanDetail(loaneeLoanDetail)
                    .loaneeStatus(LoaneeStatus.ADDED)
                    .onboardingMode(OnboardingMode.FILE_UPLOADED_FOR_DISBURSED_LOANS)
                    .loanee(loanee)
                    .cohort(cohort)
                    .employmentStatus(EmploymentStatus.UNEMPLOYED)
                    .createdBy(cohort.getCreatedBy())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
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
                log.info("Loanee loan details before saving in add loanee to cohort on upload {}", cohortLoanee.getLoaneeLoanDetail());
                LoaneeLoanDetail savedLoaneeLoanDetail = loaneeLoanDetailsOutputPort.save(cohortLoanee.getLoaneeLoanDetail());
                cohortLoanee.setLoaneeLoanDetail(savedLoaneeLoanDetail);
                log.info("Loanee's loan details after saving in file upload {}", savedLoaneeLoanDetail);
                cohortLoanee.setLoaneeLoanDetail(savedLoaneeLoanDetail);

                Loanee savedLoanee = getSavedLoanee(cohortLoanee);
                updateMeedlPortfolio(savedLoanee);
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


    private void updateMeedlPortfolio(Loanee loanee) throws MeedlException {
        boolean newLoanee =  cohortLoaneeOutputPort.checkIfLoaneeIsNew(loanee.getId());
        if(newLoanee){
            Portfolio portfolio = Portfolio.builder().portfolioName(MeedlConstants.MEEDL).build();
            portfolio = portfolioOutputPort.findPortfolio(portfolio);
            portfolio.setNumberOfLoanees(portfolio.getNumberOfLoanees() + 1);
            portfolioOutputPort.save(portfolio);
        }
    }

    private Loanee getSavedLoanee(CohortLoanee cohortLoanee) throws MeedlException {
        Optional<Loanee> optionalLoaneeFound = loaneeOutputPort.findByUserId(cohortLoanee.getLoanee().getUserIdentity().getId());
        if (optionalLoaneeFound.isPresent()){
            log.info("Loanee been uploaded exist previously as a loanee ");
            cohortLoanee.getLoanee().setId(optionalLoaneeFound.get().getId());
            return cohortLoanee.getLoanee();
        }
        Loanee loanee = loaneeOutputPort.save(cohortLoanee.getLoanee());
        setUpLoaneeLoanAggregate(loanee);
        return loanee;
    }

    private void setUpLoaneeLoanAggregate(Loanee createdLoanee) throws MeedlException {
        LoaneeLoanAggregate loaneeLoanAggregate = LoaneeLoanAggregate.builder()
                .loanee(createdLoanee)
                .historicalDebt(BigDecimal.ZERO)
                .numberOfLoans(0)
                .totalAmountOutstanding(BigDecimal.ZERO)
                .totalAmountRepaid(BigDecimal.ZERO).build();
        loaneeLoanAggregateOutputPort.save(loaneeLoanAggregate);
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

    private List<Map<String, String>> readFile(LoanBook loanBook) throws MeedlException {
        List<Map<String, String>> data;
        loanBookValidator.validateFileType(loanBook);
        File file = loanBook.getFile();

        try {
            if (file.getName().endsWith(".csv")) {
                log.info("The file type is .csv");
                data = validateAndReadCSV(loanBook);
            } else if (file.getName().endsWith(".xlsx") || file.getName().endsWith(".xls")) {
                log.info("The file is an Excel file, converting to CSV");
                File convertedCsv = convertExcelToCsv(file);
                loanBook.setFile(convertedCsv);
                data = validateAndReadCSV(loanBook);
                convertedCsv.deleteOnExit();
            } else {
                log.error("Unsupported file type.");
                throw new LoanException("Unsupported file type.");
            }
        } catch (IOException e) {
            log.error("Error occurred while processing file", e);
            throw new MeedlException(e.getMessage());
        }

        return data;
    }
    private File convertExcelToCsv(File excelFile) throws IOException {
        Workbook workbook = WorkbookFactory.create(excelFile);
        Sheet sheet = workbook.getSheetAt(0);

        File tempCsvFile = File.createTempFile("converted-", ".csv");
        try (PrintWriter writer = new PrintWriter(tempCsvFile)) {
            for (Row row : sheet) {
                List<String> cells = new ArrayList<>();
                for (Cell cell : row) {
                    cell.setCellType(CellType.STRING);
                    cells.add(cell.getStringCellValue().replaceAll(",", " "));
                }
                String csvLine = String.join(",", cells);
                log.info("Writing CSV line: {}", csvLine);
                writer.println(csvLine);
            }
        } finally {
            workbook.close();
        }
        return tempCsvFile;
    }

    private List<Map<String, String>> validateAndReadCSV(LoanBook loanBook) throws IOException, MeedlException {
        List<Map<String, String>> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(loanBook.getFile()))) {
            String headerLine = br.readLine();

            Map<String, Integer> headerIndexMap = getAndValidateFileHeaderMap(loanBook, headerLine);

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] values = line.split(",");
                log.info("The row line to get has {}", Arrays.toString(values));
                Map<String, String> rowMap = new HashMap<>();

                for (String header : loanBook.getRequiredHeaders()) {
                    log.info("The header to get its value : {}", header);
                    if (!headerIndexMap.containsKey(header)) {
                        log.warn("Skipping missing header: {}", header);
                        continue;
                    }
                    int index = headerIndexMap.get(header);
                    if (loanBookValidator.isValueNotPresentInColumn(header, index, values)) continue;
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


    private Map<String, Integer> getAndValidateFileHeaderMap(LoanBook loanBook, String headerLine) throws MeedlException {
        if (headerLine == null) {
            log.info("CSV file is empty or missing headers.");
            throw new LoanException("CSV file is empty or missing headers.");
        }

        log.info("Header line first read {}", headerLine);
        String[] headers = headerLine.split(",");
        log.info("Headers splited into a list {}", Arrays.toString(headers));
        Map<String, Integer> headerIndexMap = new HashMap<>();

        extractFileHeaderMap(headers, headerIndexMap);

        loanBookValidator.validateFileHeader(loanBook, headerIndexMap);
//        validateFileHeader(requiredHeaders, headerIndexMap);
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
                    || required.equals("middlename")){
                continue;
            }
            if (!headerIndexMap.containsKey(required)) {
                log.error("Missing required column {}, Provided headers are {}", required, headerIndexMap);
                throw new LoanException("Missing required column: " + required);
            }
        }
    }


    private List<String> getUserDataUploadHeaders() {
        return List.of("firstname", "lastname", "middlename",
                "email", "phonenumber", "initialdeposit",
                "loanstartdate",
                "amountrequested", "amountreceived",
                "bvn", "nin",
                "loanproduct");
    }
    private List<String> getRepaymentRecordUploadRequiredHeaders() {
        return List.of(
                "email", "paymentdate",
                "amountpaid");
    }

}
