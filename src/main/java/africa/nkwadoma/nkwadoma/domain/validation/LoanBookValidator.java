package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.aes.AesOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanBookValidator {
    private final LoaneeOutputPort loaneeOutputPort;
    private final LoanProductOutputPort loanProductOutputPort;
    private final AesOutputPort aesOutputPort;
    private final AsynchronousNotificationOutputPort asynchronousNotificationOutputPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final CohortUseCase cohortUseCase;
    private MeedlNotification repaymentHistoryFailureNotification;
    private StringBuilder validationErrorMessage;

    public void validateUserDataUploadFile(LoanBook loanBook, List<Map<String, String>> data, List<String> requiredHeaders) {

    }
    public void setValidationErrorMessage(){
        validationErrorMessage = new StringBuilder();
    }
    public void validateUserDataFileHeader(LoanBook loanBook, List<String> requiredHeaders, Map<String, Integer> headerIndexMap){
        for (String required : requiredHeaders) {
            if (!headerIndexMap.containsKey(required)) {
//                throw new MeedlException("Missing required column: " + required);
            }
        }
    }
    public void verifyUserExistInCohort(LoanBook loanBook){

    }

    public void validateAllFileFields(List<CohortLoanee> convertedLoanees) throws MeedlException {
        int rowCount = 1;
        for (CohortLoanee cohortLoanee : convertedLoanees) {
            validateFileBvn(cohortLoanee.getLoanee().getUserIdentity());
            validateFileNin(cohortLoanee.getLoanee().getUserIdentity());
            validatePhoneNumber(cohortLoanee.getLoanee().getUserIdentity());
            validateNames(cohortLoanee.getLoanee().getUserIdentity());
            validateLoanProductExist(cohortLoanee.getLoanee());
            validateAmount(cohortLoanee.getLoanee(), rowCount);
            rowCount++;
        }

    }

    private void validatePhoneNumber(UserIdentity userIdentity) throws MeedlException {
        String phoneNumber = formatPhoneNumber(userIdentity.getPhoneNumber());
        MeedlValidator.validateElevenDigits(phoneNumber,"User with email "+userIdentity.getEmail()+ " has invalid phone number.");
    }
    public String formatPhoneNumber(String input) {
        if (input != null && input.matches("^\\d{10}$")) {
            return "0" + input;
        }
        return input;
    }
    private void validateNames(UserIdentity userIdentity) throws MeedlException {
        String email = userIdentity.getEmail();
        validateName(userIdentity.getFirstName(), "First name can not be empty for "+email,email+" first name");
        validateName(userIdentity.getLastName(), "Last name can not be empty for "+email,email+" last name");
        if (MeedlValidator.isNotEmptyString(userIdentity.getMiddleName())){
            validateName(userIdentity.getMiddleName(), "Middle name can not be empty for "+email,email+" middle name");
        }
    }

    private void validateName(String nameToValidate, String message ,String attributeName) throws MeedlException {
        MeedlValidator.validateObjectName(nameToValidate, message , attributeName);
    }

    private void validateAmount(Loanee loanee, int rowCount) throws MeedlException {
        validateMoneyValue(loanee.getLoaneeLoanDetail().getInitialDeposit(), "Initial deposit for user with email "+loanee.getUserIdentity().getEmail()+" is invalid: "+ convertIfNull( loanee.getLoaneeLoanDetail().getInitialDeposit()), rowCount);
        validateMoneyValue(loanee.getLoaneeLoanDetail().getAmountRequested(), "Amount requested for user with email "+loanee.getUserIdentity().getEmail()+" is invalid: "+ convertIfNull( loanee.getLoaneeLoanDetail().getAmountRequested()), rowCount);
        validateMoneyValue(loanee.getLoaneeLoanDetail().getAmountReceived(), "Amount received for user with email "+loanee.getUserIdentity().getEmail()+" is invalid: "+ convertIfNull( loanee.getLoaneeLoanDetail().getAmountReceived()), rowCount);
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
//            throw new MeedlException(message);
        }
    }

    private void validateFileNin(UserIdentity userIdentity) throws MeedlException {
        String encryptedNin = validateFileAndEncryptBvnOrNin(userIdentity.getNin(), "User with email "+userIdentity.getEmail()+" has invalid or missing nin "+userIdentity.getNin());
        userIdentity.setNin(encryptedNin);
        log.info("nin successfully validated and encrypted");
    }

    private void validateFileBvn(UserIdentity userIdentity) throws MeedlException {
        String encryptedBvn = validateFileAndEncryptBvnOrNin(userIdentity.getBvn(), "User with email "+userIdentity.getEmail()+" has invalid or missing bvn "+userIdentity.getBvn() );
        userIdentity.setBvn(encryptedBvn);
        log.info("Bvn successfully validated and encrypted");

    }
    private String validateFileAndEncryptBvnOrNin(String bvnOrNin, String errorMessage) throws MeedlException {
        MeedlValidator.validateElevenDigits(bvnOrNin, errorMessage);

        return encryptValue(bvnOrNin, errorMessage);
    }
    private String encryptValue(String value, String errorMessage) {
        try {
            MeedlValidator.validateElevenDigits(value, errorMessage);
            return aesOutputPort.encryptAES(value.trim());
        } catch (MeedlException e) {
            log.error("Unable to encrypt value {}", value);
        }
        return StringUtils.EMPTY;
    }


    private void validateLoanProductExist(Loanee loanee) throws MeedlException {
        boolean loanProductExist = loanProductOutputPort.existsByNameIgnoreCase(loanee.getLoanProductName());
        if (!loanProductExist) {
            log.error("Loan Product with name {} does not exist for user with email {}", loanee.getCohortName(), loanee.getUserIdentity().getEmail());
            throw new MeedlException("Loan product with name " + loanee.getCohortName() + " does not exist for user with email "+ loanee.getUserIdentity().getEmail());
        }
        log.info("Loan product exists with name {}", loanee.getLoanProductName());
    }

    public void repaymentHistoryValidation(List<Map<String, String>> data, LoanBook repaymentHistoryBook) throws MeedlException {
        repaymentHistoryFailureNotification = new MeedlNotification();
        validationErrorMessage = new StringBuilder();
        validateCohortExists(repaymentHistoryBook.getCohort());
        int rowCount = 1;
        for (Map<String, String> row : data) {

            validateDateTimeFormat(row, "paymentdate", rowCount);
//            validateAmountPaid(row, "amountpaid", rowCount);
            validateAmountPaid(row.get("amountpaid"), rowCount);
            validateUserExistForRepayment(row, "email", rowCount);
            rowCount++;
        }
        hasFailure(repaymentHistoryBook);

    }

    private void validateCohortExists(Cohort cohort) {
        try {
            Cohort foundCohort = findCohort(cohort);
            log.info("Cohort was found successfully in upload repayment history validation {}", foundCohort);
        } catch (MeedlException e) {
            log.error("Cohort in upload repayment validation not found. error : {}", e.getMessage());
            validationErrorMessage.append("Error finding cohort with message: ").append(e.getMessage()).append(". \n ");
        }
    }
    private Cohort findCohort(Cohort cohort) throws MeedlException {
        MeedlValidator.validateObjectInstance(cohort, CohortMessages.COHORT_CANNOT_BE_EMPTY.getMessage());
        return cohortUseCase.viewCohortDetails(cohort.getCreatedBy(), cohort.getId());
    }



    private void hasFailure(LoanBook repaymentHistoryBook) throws MeedlException {
        if (validationErrorMessage!= null && !validationErrorMessage.toString().isBlank()) {
            log.warn("Validation Error: {}", validationErrorMessage);
            buildFailureNotification(repaymentHistoryBook);
            throw new MeedlException("One or multiple Errors Occures.");
        }
        log.info("No errors was found during the upload.");
    }

    private void buildFailureNotification(LoanBook repaymentHistoryBook) throws MeedlException {
        MeedlNotification failureNotification =  MeedlNotification.builder()
                .build();
        UserIdentity foundActor = userIdentityOutputPort.findById(repaymentHistoryBook.getActorId());
        asynchronousNotificationOutputPort.notifyPmForLoanRepaymentUploadFailure(foundActor, validationErrorMessage, repaymentHistoryBook.getFile().getName());

    }

    public void validateDateTimeFormat(Map<String, String> row, String dateName, int rowCount) throws MeedlException {
                String dateStr = row.get(dateName);

                LocalDateTime parsedDate = parseFlexibleDateTime(dateStr, rowCount);
                log.info("Parsed date: {}", parsedDate);
    }
    private void validateAmountPaid(String amountPaid, int rowCount) {
            if (amountPaid == null || amountPaid.trim().isEmpty()) {
                validationErrorMessage.append("Error row : ").append(rowCount).append(" Monetary value is required.");
            }

            try {
                BigDecimal value = new BigDecimal(amountPaid.trim());

                if (value.compareTo(BigDecimal.ZERO) < 0) {
                    validationErrorMessage.append("Error row : ").append(rowCount).append(" Monetary value cannot be negative.");
                }

            } catch (NumberFormatException e) {
                validationErrorMessage.append("Error row : ").append(rowCount).append(" Invalid monetary value. Must be a number.");
            } catch (Exception e) {
                validationErrorMessage.append("Error row : ").append(rowCount).append(" Unexpected error occurred while validating monetary value.");
            }

    }
    public void validateAmountPaid(Map<String, String> row, String amountPaidKey, int rowCount) throws MeedlException {

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
//           throw new MeedlException(errorMessage);
       }
    }


    public void validateUserExistForRepayment(Map<String, String> row, String email, int rowCount) throws MeedlException {

        String emailToCheck = row.get(email);

        Loanee loanee = null;
        try {

            loanee = loaneeOutputPort.findByLoaneeEmail(emailToCheck);
        } catch (MeedlException exception) {
            validationErrorMessage.append("Error in row : ")
                    .append(rowCount)
                    .append(" ").append("Loanee with email : ")
                    .append(emailToCheck)
                    .append(". ")
                    .append(exception.getMessage())
                    .append("\n");
            log.error("{}", exception.getMessage());
//            throw  new RuntimeException(exception.getMessage());
        }

        log.info("loanee found in repayment history : {}", loanee);
        if (loanee == null) {
            log.error("Loanee with email {} does not exist for repayment. For row {}", emailToCheck, rowCount);
            validationErrorMessage.append("Error in row : ")
                    .append(rowCount)
                    .append(" ").append("Loanee with email : ")
                    .append(emailToCheck)
                    .append(" does not exist for repayment.")
                    .append("\n ");
//            throw new MeedlException("Loanee with email : " + emailToCheck + " does not exist for repayment");
        }
        log.info("Loanee with email {} on row {} exist. ", emailToCheck, rowCount);

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
