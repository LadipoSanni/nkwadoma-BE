package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.LoaneeUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.UploadType;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanBookValidator {
    private final LoaneeOutputPort loaneeOutputPort;
    private final LoanProductOutputPort loanProductOutputPort;
    private final AsynchronousNotificationOutputPort asynchronousNotificationOutputPort;
    private final CohortUseCase cohortUseCase;
    private final CohortLoaneeOutputPort cohortLoaneeOutputPort;
    private final IdentityManagerOutputPort identityManagerOutputPort;
    private StringBuilder validationErrorMessage;


    public void validateLoanBookObjectValues(LoanBook loanBook, UploadType uploadType) throws MeedlException {
        validationErrorMessage = new StringBuilder();

        if (ObjectUtils.isEmpty(loanBook)){
            validationErrorMessage.append("Loan book cannot be empty.");
            log.error("{} Loan book was passed to upload {} ", loanBook, uploadType);
            try{
                sendFailureNotificationInitialLevel(uploadType);
            }catch (MeedlException e){
                log.warn("Possibly failed to send notification on upload failure initial level of ---> {}", uploadType);
                log.error("",e);
            }
            throw new MeedlException("Loan book cannot be empty");
        }
        if (ObjectUtils.isEmpty(loanBook.getCohort())){
            log.error("Cohort is empty on upload loan book. \n");
            validationErrorMessage.append("Unable to determine cohort detail. \n");
        }else if (StringUtils.isEmpty(loanBook.getCohort().getId()) || isNotValidUUID(loanBook.getCohort().getId())){
            log.error("Cohort id is not a valid uuid or its empty.");
            validationErrorMessage.append("Invalid cohort id provided. \n");
        }
        if (ObjectUtils.isEmpty(loanBook.getFile())){
            validationErrorMessage.append("Please provide file to upload \n");
        }

        if (!validationErrorMessage.toString().isBlank()) {
            log.warn("Validation Error at the top upload layer ---> {}", validationErrorMessage);
            try{
                sendFailureNotification(loanBook, uploadType);
            }catch (MeedlException e){
                log.warn("Second layer of initial validation --- Possibly failed to send notification on upload failure initial level of ---> {}", uploadType);
                log.error("",e);
            }
            throw new MeedlException("One or multiple Errors Occures.");
        }
    }



    private static boolean isNotValidUUID(String id) {
        if (id == null || id.isEmpty()) {
            return true;
        }
        try {
            UUID.fromString(id);
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }


    public void validateUserDataUploadFile(LoanBook loanBook, List<Map<String, String>> data, List<String> requiredHeaders) throws MeedlException {
        validationErrorMessage = new StringBuilder();
        boolean isValidCohort = validateCohortDetails(loanBook.getCohort());

        if (isValidCohort){
            validateLoaneeDetails(loanBook, data);
        }
        hasFailure(loanBook, UploadType.USER_DATA);
    }

    private void validateLoaneeDetails(LoanBook loanBook, List<Map<String, String>> data) {
        int rowCount = 1;
        for (Map<String, String> row : data) {

            validateElevenDigit(row.get("bvn"), "Invalid bvn : "+rowCount);
            validateElevenDigit(row.get("nin"), "Invalid nin row : "+rowCount);
            validateElevenDigit(row.get("phonenumber"), "Invalid phone number row : "+rowCount);

            validateLoaneeDoesNotExistInTheSameCohort(row.get("email"), loanBook.getCohort(), rowCount );

            validateName(rowCount, row.get("firstname"), "First name");
            validateName(rowCount, row.get("lastname"), "Last name");

            if (MeedlValidator.isNotEmptyString(row.get("middlename"))){
                validateName(rowCount, row.get("middlename"), "Middle name");
            }
            validateLoanProductExist(row.get("loanproduct"), rowCount);

            log.info("initial deposit --- {}, amount requested ---- {}, amount received {}",row.get("initialdeposit"), row.get("amountrequested"), row.get("amountreceived"));
            validateMonetaryValue(row.get("initialdeposit"), rowCount);
            validateMonetaryValue(row.get("amountrequested"), rowCount);
            validateMonetaryValue(row.get("amountreceived"), rowCount);

            validateInitialDepositAndAmountApproved(row.get("initialdeposit"), row.get("amountreceived"), rowCount);
            rowCount++;
        }
    }

    private void validateLoaneeDoesNotExistInTheSameCohort(String email, Cohort cohort, int rowCount) {
        Loanee loaneeFound = null;
        try {
            loaneeFound = loaneeOutputPort.findByLoaneeEmail(email);
        } catch (MeedlException e) {
            log.info("Loanee with email {} does not exist on the platform. Proceed to upload user data ", email, e);
        }
        if (ObjectUtils.isNotEmpty(loaneeFound)){
            checkIfLoaneeExistInTheSameCohort(cohort, loaneeFound, rowCount);
        }
    }

    private void checkIfLoaneeExistInTheSameCohort(Cohort cohort, Loanee loanee, int rowCount) {
        CohortLoanee cohortLoanee = null;
        try {
            cohortLoanee = cohortLoaneeOutputPort.findCohortLoaneeByLoaneeIdAndCohortId(loanee.getId(), cohort.getId());
        } catch (MeedlException e) {
            log.info("Uploaded loanee {} does not exist in this cohort {}", loanee.getId(), cohort.getId());
        }

        if (ObjectUtils.isNotEmpty(cohortLoanee)) {
            validationErrorMessage
                    .append("Error in row : ")
                    .append(rowCount)
                    .append(" ")
                    .append(LoaneeMessages.LOANEE_WITH_EMAIL_EXIST_IN_COHORT.getMessage())
                    .append("\n");
        }
    }

    public void setValidationErrorMessage(){
        validationErrorMessage = new StringBuilder();
    }


    private void validateInitialDepositAndAmountApproved(String initialDepositString, String amountReceivedString, int rowCount){
        boolean isNotInitialDepositValid = moneyStringIsNotValid(initialDepositString);
        boolean isNotAmountReceivedValid = moneyStringIsNotValid(amountReceivedString);
        if (isNotInitialDepositValid || isNotAmountReceivedValid){
            log.error("Error : Values passed for monetary values are {} ----- and ------ {}", initialDepositString
            , amountReceivedString);
            validationErrorMessage.append("Error row : ")
                    .append(rowCount).append(" Monetary value is required.")
                    .append("\n");
            return;
        }

        BigDecimal initialDeposit = new BigDecimal(initialDepositString);
        BigDecimal amountReceived = new BigDecimal(amountReceivedString);
        if (ObjectUtils.isNotEmpty(initialDeposit) && ObjectUtils.isNotEmpty(amountReceived)) {
            if (initialDeposit.compareTo(amountReceived) > 0) {
                log.error("Initial deposit: {} cannot be greater than amount received: {}", initialDeposit, amountReceived);
                validationErrorMessage.append("Initial deposit cannot be greater than amount received. Row : ")
                        .append(rowCount)
                        .append("\n");
//                throw new MeedlException("Initial deposit cannot be greater than amount received");
            }
        }
    }

    public String formatPhoneNumber(String input) {
        if (input != null && input.matches("^\\d{10}$")) {
            return "0" + input;
        }
        return input;
    }


    private void validateName(int rowCount, String nameToValidate, String attributeName) {
        try {
            MeedlValidator.validateObjectName(nameToValidate, "Invalid name" , attributeName);
        } catch (MeedlException e) {
            log.error("Error during user data upload name validation ", e);
            validationErrorMessage.append("Error in ")
                    .append(attributeName)
                    .append(" Row : ")
                    .append(rowCount)
                    .append("\n");
        }
    }

    public String convertIfNull(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return "Value not provided";
        }
        return bigDecimal.toString();
    }
    public String convertIfNull(String amount) {
        if (MeedlValidator.isEmptyString(amount)) {
            return "Value not provided";
        }
        return amount;
    }

    public void validateMoneyValue(BigDecimal amount, String message, int rowCount) throws MeedlException {
        MeedlValidator.validateBigDecimalDataElement(amount, message);
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Negative amount detected: {} {}", amount, message);
            validationErrorMessage.append("Error in row : ").append(rowCount).append(" ").append(message).append("\n");
            throw new MeedlException(message);
        }
    }

    private void validateElevenDigit(String elevenDigitNumber, String errorMessage)  {
        elevenDigitNumber = formatPhoneNumber(elevenDigitNumber);
        try {
            MeedlValidator.validateElevenDigits(elevenDigitNumber, errorMessage);
        } catch (MeedlException e) {
            validationErrorMessage.append(errorMessage)
                    .append(" \n");
        }
        log.info("Eleven digit number successfully validated and encrypted ");

    }

    private void validateLoanProductExist(String loanProductName, int rowCount){
        boolean loanProductExist = false;
        try {
            loanProductExist = loanProductOutputPort.existsByNameIgnoreCase(loanProductName);
        } catch (MeedlException e) {
            validationErrorMessage.append(e.getMessage())
                    .append(" Row : ")
                    .append(rowCount);
        }
        if (!loanProductExist) {
            log.error("Loan Product with name {} does not exist user data upload", loanProductName);
            validationErrorMessage.append("Loan product with name ")
                    .append(loanProductName)
                    .append(" does not exist. Row ")
                    .append(rowCount)
                    .append(".\n");
//            throw new MeedlException("Loan product with name " + loanProductName + " does not exist  ");
        }
        log.info("Loan product exists with name {}", loanProductName);
    }

    public void repaymentHistoryValidation(List<Map<String, String>> data, LoanBook repaymentHistoryBook) throws MeedlException {
        validationErrorMessage = new StringBuilder();
        validateCohortDetails(repaymentHistoryBook.getCohort());
        int rowCount = 1;
        for (Map<String, String> row : data) {

            validateDateTimeFormat(row, "paymentdate", rowCount);
            validateMonetaryValue(row.get("amountpaid"), rowCount);
            validateUserExistByEmail(row.get("email"), rowCount);
            rowCount++;
        }
        hasFailure(repaymentHistoryBook, UploadType.REPAYMENT);

    }

    private boolean validateCohortDetails(Cohort cohort) {
        boolean isCohortValid = Boolean.TRUE;
        try {
            Cohort foundCohort = findCohort(cohort);
            log.info("Cohort was found successfully in upload repayment history validation {}", foundCohort);
            isCohortValid = checkIfCohortTuitionDetailsHaveBeenUpdated(foundCohort);
        } catch (MeedlException e) {
            log.error("Cohort in upload repayment validation not found. error : {}", e.getMessage());
            validationErrorMessage.append("Error uploading data : ").append(e.getMessage()).append(". \n ");
            isCohortValid = Boolean.FALSE;
        }
        return isCohortValid;

    }

    private Boolean checkIfCohortTuitionDetailsHaveBeenUpdated(Cohort cohort) {
        if (ObjectUtils.isEmpty(cohort.getTuitionAmount())) {
            log.info("Cohort does not have any cohort tuition details. Cohort id: {}", cohort.getId());
            validationErrorMessage.append(CohortMessages.COHORT_TUITION_DETAILS_MUST_HAVE_BEEN_UPDATED.getMessage());
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }


    private Cohort findCohort(Cohort cohort) throws MeedlException {
        MeedlValidator.validateObjectInstance(cohort, CohortMessages.COHORT_CANNOT_BE_EMPTY.getMessage());
        return cohortUseCase.viewCohortDetails(cohort.getCreatedBy(), cohort.getId());
    }



    private void hasFailure(LoanBook loanBook, UploadType uploadType) throws MeedlException {
        if (ObjectUtils.isNotEmpty(validationErrorMessage) && !validationErrorMessage.toString().isBlank()) {
            log.warn("Validation Error ---> {}", validationErrorMessage);
            sendFailureNotification(loanBook, uploadType);
            throw new MeedlException("One or multiple Errors Occures.");
        }
        log.info("No errors was found during the upload.");
    }

    private void sendFailureNotification(LoanBook loanBook, UploadType uploadType) throws MeedlException {
        UserIdentity foundActor = identityManagerOutputPort.getUserById(loanBook.getActorId());
        if (uploadType.equals(UploadType.REPAYMENT)){
            log.info("Notify pm of REPAYMENT data upload failure");
            asynchronousNotificationOutputPort.notifyPmForLoanRepaymentUploadFailure(foundActor, validationErrorMessage, loanBook);
        }else if (uploadType.equals(UploadType.USER_DATA)){
            log.info("Notify pm of USER data upload failure");
            asynchronousNotificationOutputPort.notifyPmForUserDataUploadFailure(foundActor, validationErrorMessage, loanBook);
        }
    }
    private void sendFailureNotificationInitialLevel(UploadType uploadType) throws MeedlException {
        if (uploadType.equals(UploadType.REPAYMENT)){
            log.info("Notify all pm of REPAYMENT data upload failure with possible malicious attempt");
            asynchronousNotificationOutputPort.notifyAllPmForLoanRepaymentUploadFailure(validationErrorMessage);
        }else if (uploadType.equals(UploadType.USER_DATA)){
            log.info("Notify pm of USER data upload failure, with possible malicious attempt");
            asynchronousNotificationOutputPort.notifyAllPmForUserDataUploadFailure(validationErrorMessage);
        }
    }

    public void validateDateTimeFormat(Map<String, String> row, String dateName, int rowCount) throws MeedlException {
                String dateStr = row.get(dateName);

                LocalDateTime parsedDate = parseFlexibleDateTime(dateStr, rowCount);
                log.info("Parsed date: {}", parsedDate);
    }
    private void validateMonetaryValue(String moneyStringValue, int rowCount) {
        if (moneyStringIsNotValid(moneyStringValue)) {
            log.error("Error : Value passed for monetary value string is : {}", moneyStringValue);
            validationErrorMessage.append("Error row : ")
                    .append(rowCount).append(" Monetary value is required.")
                    .append("\n");
            return;
        }

        try {
                BigDecimal value = new BigDecimal(moneyStringValue);

                if (value.compareTo(BigDecimal.ZERO) < 0) {
                    validationErrorMessage.append("Error row : ")
                            .append(rowCount).append(" Monetary value cannot be negative.")
                            .append("\n");
                }

            } catch (NumberFormatException e) {
                validationErrorMessage.append("Error row : ")
                        .append(rowCount).append(" Invalid monetary value. Must be a number ")
                        .append("\n");
            } catch (Exception e) {
                validationErrorMessage.append("Error row : ").append(rowCount)
                        .append(" Unexpected error occurred while validating monetary value.")
                        .append("\n");
            }

    }

    private boolean moneyStringIsNotValid(String amountPaid) {
        return amountPaid == null || amountPaid.trim().isEmpty();
    }

    public void validateMonetaryValue(Map<String, String> row, String amountPaidKey, int rowCount) throws MeedlException {

            String amountPassed = row.get(amountPaidKey);

            containsOnlyDigits(amountPassed, "Amount paid is not a monetary value. "+convertIfNull(amountPassed), rowCount);
            validateMoneyValue(new BigDecimal(amountPassed), "Amount repaid is required.", rowCount);
            log.info("Amount validated for amount paid: {} Row : {}", amountPassed, rowCount);

    }
    public void containsOnlyDigits(String input, String errorMessage, int rowCount) throws MeedlException {
       boolean isOnlyDigits = input != null && input.matches("\\d+(\\.\\d+)?");
       if (!isOnlyDigits){
           log.error("Its not only digits {}", input);
           validationErrorMessage.append("Error in row : ").append(rowCount).append(" ").append(errorMessage).append("\n");
           throw new MeedlException(errorMessage);
       }
    }


    public void validateUserExistByEmail(String email,int rowCount) {
        Loanee loanee = null;
        try {

            loanee = loaneeOutputPort.findByLoaneeEmail(email);
        } catch (MeedlException exception) {
            validationErrorMessage.append("Error in row : ")
                    .append(rowCount)
                    .append(" ")
                    .append(exception.getMessage())
                    .append("\n");
            log.error("{}", exception.getMessage());
        }

        log.info("loanee found in repayment history : {}", loanee);
        if (loanee == null) {
            log.error("Loanee with email {} does not exist for repayment. For row {}", email, rowCount);
            validationErrorMessage.append("Error in row : ")
                    .append(rowCount)
                    .append(" ").append("Loanee with email : ")
                    .append(email)
                    .append(" does not exist for repayment.")
                    .append("\n ");
        }
        log.info("Loanee with email {} on row {} exist. ", email, rowCount);

    }
    private LocalDateTime parseFlexibleDateTime(String dateStr, int rowCount) throws MeedlException {
        log.info("Repayment date before formating in validation service {}", dateStr);
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
        validationErrorMessage.append("Error on row : ").append(rowCount).append(" Date doesn't match format. Date provided is : ").append(dateStr).append(". Example date format : 1/10/2019 -- mm/dd/yyyy. \n ");
//        throw new MeedlException("Date doesn't match format. Date: "+dateStr + " Example format : 21/10/2019");
        return null;
    }

}
