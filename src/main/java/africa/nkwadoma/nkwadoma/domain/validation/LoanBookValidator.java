package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.application.ports.output.aes.AesOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class LoanBookValidator {
    private final LoaneeOutputPort loaneeOutputPort;
    private final LoanProductOutputPort loanProductOutputPort;
    private final AesOutputPort aesOutputPort;

    public void validateUserDataUploadFile(LoanBook loanBook, List<Map<String, String>> data, List<String> requiredHeaders) {

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

    public void validateAllFileFields(List<Loanee> convertedLoanees) throws MeedlException {
        for (Loanee loanee : convertedLoanees) {
            validateFileBvn(loanee.getUserIdentity());
            validateFileNin(loanee.getUserIdentity());
            validateNames(loanee.getUserIdentity());
            validateLoanProductExist(loanee);
            validateAmount(loanee);
        }

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

    private void validateAmount(Loanee loanee) throws MeedlException {
        validateMoneyValue(loanee.getLoaneeLoanDetail().getInitialDeposit(), "Initial deposit for user with email "+loanee.getUserIdentity().getEmail()+" is invalid: "+ convertIfNull( loanee.getLoaneeLoanDetail().getInitialDeposit()));
        validateMoneyValue(loanee.getLoaneeLoanDetail().getAmountRequested(), "Amount requested for user with email "+loanee.getUserIdentity().getEmail()+" is invalid: "+ convertIfNull( loanee.getLoaneeLoanDetail().getAmountRequested()));
        validateMoneyValue(loanee.getLoaneeLoanDetail().getAmountReceived(), "Amount received for user with email "+loanee.getUserIdentity().getEmail()+" is invalid: "+ convertIfNull( loanee.getLoaneeLoanDetail().getAmountReceived()));
    }

    private String convertIfNull(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return "Value not provided";
        }
        return bigDecimal.toString();
    }

    public void validateMoneyValue(BigDecimal amount, String message) throws MeedlException {
        MeedlValidator.validateBigDecimalDataElement(amount, message);
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Negative amount detected: {} {}", amount, message);
            throw new MeedlException(message);
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
        MeedlValidator.validateBvnOrNin(bvnOrNin, errorMessage);

        return encryptValue(bvnOrNin, errorMessage);
    }
    private String encryptValue(String value, String errorMessage) {
        try {
            MeedlValidator.validateBvnOrNin(value, errorMessage);
            return aesOutputPort.encryptAES(value.trim());
        } catch (MeedlException e) {
            log.error("Unable to encrypt value {}", value);
        }
        return StringUtils.EMPTY;
    }


    private void validateLoanProductExist(Loanee loanee) throws MeedlException {
        boolean loanProductExist = loanProductOutputPort.existsByNameIgnoreCase(loanee.getCohortName());
        if (!loanProductExist) {
            log.error("Loan Product with name {} does not exist for user with email {}", loanee.getCohortName(), loanee.getUserIdentity().getEmail());
            throw new MeedlException("Loan product with name " + loanee.getCohortName() + " does not exist for user with email "+ loanee.getUserIdentity().getEmail());
        }
    }

    public void validateDateTimeFormat(List<Map<String, String>> data, String dateName) throws MeedlException {
            for (Map<String, String> row : data) {
                String dateStr = row.get(dateName);

                LocalDateTime parsedDate = parseFlexibleDateTime(dateStr);
                log.info("Parsed date: {}", parsedDate);

        }
    }
    private LocalDateTime parseFlexibleDateTime(String dateStr) throws MeedlException {
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
        throw new MeedlException("Date doesn't match format. Date: "+dateStr + " Example format : 21/10/2019");
    }
}
