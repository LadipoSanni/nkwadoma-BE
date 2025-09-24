package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.notification.MeedlNotificationMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.math.*;
import java.util.*;
import java.util.regex.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages.*;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages.*;

@Slf4j
public class MeedlValidator {

    public static void validateEmail(String email) throws MeedlException {
        if (isEmptyString(email) || !EmailValidator.getInstance().isValid(email.trim())) {
            log.info("Invalid email address provided : {}", email);
            throw new MeedlException(MeedlMessages.INVALID_EMAIL_ADDRESS.getMessage());
        }
    }

    public static void validateLoanDecision(String loanReferralStatus) throws MeedlException {
        boolean matches = Pattern.matches("^(ACCEPTED|DECLINED)$", loanReferralStatus);
        if (!matches) {
            throw new MeedlException(LoanMessages.INVALID_LOAN_DECISION.getMessage());
        }
    }
    public static boolean isNotValidId(String id) {
        return !isValidId(id);
    }
    public static boolean isValidId(String id) {
        return !isEmptyString(id) && isValidUUid(id);
    }
    private static boolean isValidUUid(String id){
        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException e) {
            log.info("{}. The invalid UUID : {}", e.getMessage(), id);
            return false;
        }
    }


    public static void validateUUID(String dataElement, String message) throws MeedlException {
        log.info("validateUUID {}", dataElement);
        validateDataElement(dataElement, message);
        if (!isValidUUID(dataElement)) {
            log.info("{}. The invalid UUID : {}", dataElement, message);
            throw new MeedlException(message);
        }
    }

    public static void validateDataElement(String dataElement, String message) throws MeedlException  {
        if (isEmptyString(dataElement)) {
            log.error(message);
            throw new MeedlException(message);
        }
    }

    public static boolean isEmptyString(String dataElement) {
        return StringUtils.isEmpty(dataElement) || StringUtils.isBlank(dataElement);
    }


    public static boolean isNotEmptyString(String dataElement) {
        return !isEmptyString(dataElement);
    }

    public static void validateBigDecimalDataElement(BigDecimal dataElement) throws MeedlException {
        if (dataElement == null) {
            throw new MeedlException(MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage());
        }
    }

    public static void validateBigDecimalDataElement(BigDecimal dataElement, String message) throws MeedlException {
        if (dataElement == null) {
            throw new MeedlException(message);
        }
    }

    public static void validateFloatDataElement(Float dataElement, String message) throws MeedlException {
        if (dataElement == null || dataElement < 0) {
            throw new MeedlException(message);
        }
    }

    public static void validateIntegerDataElement(int dataElement , String message) throws MeedlException {
        if (dataElement < BigInteger.ONE.intValue()) {
            throw new MeedlException(message);
        }
    }
    public static void validateCollection(Object collection, String message) throws MeedlException {
        if (collection == null || isEmpty(collection)) {
            log.error("Collection validation failed {}", message);
            throw new MeedlException(message);
        }
    }
    public static boolean isEmptyCollection(Object collection) {
        if (collection == null || isEmpty(collection)) {
            log.warn("It is an empty collection");
            return Boolean.TRUE;
        }
        log.info("It is not an empty collection ");
        return Boolean.FALSE;
    }
    public static boolean isNotEmptyCollection(Object collection) {
        return !isEmptyCollection(collection);
    }

    public static boolean isEmpty(Object obj) {
        if (obj instanceof Collection<?>) {
            return ((Collection<?>) obj).isEmpty();
        }
        return true;
    }

    public static void validateObjectInstance(Object instance, String message) throws MeedlException {
        if (ObjectUtils.isEmpty(instance)){
            log.error("Object instance validation failed. {}", message);
            throw new MeedlException(message);
        }
    }

    public static void validatePageNumber(int pageNumber) throws MeedlException {
        if (pageNumber < BigInteger.ZERO.intValue()) {
            throw new MeedlException(MeedlMessages.PAGE_NUMBER_CANNOT_BE_LESS_THAN_ZERO.getMessage());
        }
    }

    public static void validatePageSize(int pageSize) throws MeedlException {
        if (pageSize < BigInteger.ONE.intValue()) {
            throw new MeedlException(MeedlMessages.PAGE_SIZE_CANNOT_BE_LESS_THAN_ONE.getMessage());
        }
    }
    public static String formatPhoneNumber(String input) {
        if (input != null && input.matches("^\\d{10}$")) {
            return "0" + input;
        }
        return input;
    }

    public static void validateElevenDigits(String numbersToValidate, String errorMessage) throws MeedlException {
        MeedlValidator.validateDataElement(numbersToValidate, errorMessage);
        String regex = "^\\d{11}$";

        boolean isValid = Pattern.matches(regex, numbersToValidate.trim());
        if (!isValid) {
            log.error(" {} - {}", numbersToValidate, errorMessage);
            throw new MeedlException(errorMessage);
        }
    }
    public static void validateDoubleDataElement(Double dataElement, String message) throws MeedlException {
        if (dataElement == null) {
            throw new MeedlException(message);
        }
    }
    public static void validateDoubleDataElement(Double dataElement) throws MeedlException {
        if (dataElement == null) {
            throw new MeedlException(MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage());
        }
    }

    public static void validatePassword(String password) throws MeedlException {
        validateDataElement(password, "Password can not be empty");
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN.getMessage());
        if (!pattern.matcher(password).matches()) {
            throw new MeedlException(WEAK_PASSWORD.getMessage());
        }
    }
    public static void validateObjectName(String name,  String message, String attributeName) throws MeedlException {
        MeedlValidator.validateDataElement(name, message);
        String regex =  "^(?=.*[A-Za-z])(?=.*['A-Za-z])(?!-)(?!.*-$)[A-Za-z0-9' _-]+$";
        Pattern pattern = Pattern.compile(regex);
        boolean isValid = pattern.matcher(name).matches();
        if (!isValid){
            log.error("Invalid name pattern: {}", name);
            throw new MeedlException(attributeName+" name must include letters and can only contain letters, numbers, spaces - _ '");
        }
    }
    public static void validateBankPartnerName(String name,  String message, String attributeName) throws MeedlException {
        MeedlValidator.validateDataElement(name, message);
        String regex =  "^(?=.*[A-Za-z])(?=.*['A-Za-z])[A-Za-z0-9' ()_-]+$";
        Pattern pattern = Pattern.compile(regex);
        boolean isValid = pattern.matcher(name).matches();
        if (!isValid){
            log.error("Invalid name pattern: {}", name);
            throw new MeedlException(attributeName+" name must include letters and can only contain letters, numbers, spaces - _ '");
        }
    }
    public static void validateEmailDomain(String inviteeEmail, String inviterEmail) throws MeedlException {
        MeedlValidator.validateEmail(inviteeEmail);
        MeedlValidator.validateEmail(inviterEmail);
        if (!compareEmailDomain(inviteeEmail,inviterEmail)){
            log.error("{} - {} : {}",DOMAIN_EMAIL_DOES_NOT_MATCH.getMessage(), inviteeEmail, inviterEmail);
            throw new MeedlException(DOMAIN_EMAIL_DOES_NOT_MATCH.getMessage());
        }
    }

    private static boolean compareEmailDomain(String inviteeEmail, String inviterEmail) {
        String inviteeEmailDomain =
                inviteeEmail.substring(inviteeEmail.indexOf(EMAIL_INDEX.getMessage()));
        String inviterEmailDomain = inviterEmail.substring(inviterEmail.indexOf(EMAIL_INDEX.getMessage()));
        return StringUtils.equals(inviterEmailDomain, inviteeEmailDomain);
    }
    public static void validateOrganizationUserIdentities(List<OrganizationEmployeeIdentity> employees) throws MeedlException {
        log.info("Started validating for user identities (List) : {}", employees);
        log.info("validating to check for empty list : {}", CollectionUtils.isEmpty(employees));
        if (CollectionUtils.isEmpty(employees)){
            log.error("{} - {}", USER_IDENTITY_CANNOT_BE_NULL.getMessage(), employees);
            throw new MeedlException(USER_IDENTITY_CANNOT_BE_NULL.getMessage());
        }
        for(OrganizationEmployeeIdentity employee : employees){
            MeedlValidator.validateObjectInstance(employee, IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL.getMessage());
            employee.getMeedlUser().validate();
        }
        log.info("Users identity validation completed... for user {} ", employees);
    }

    public static void validateLoanRequest(LoanRequest foundLoanRequest) throws MeedlException {
        if (ObjectUtils.isEmpty(foundLoanRequest)){
            log.info("Loan request: {}", foundLoanRequest);
            throw new MeedlException(LoanMessages.LOAN_REQUEST_NOT_FOUND.getMessage());
        }
    }

    public static void validateNegativeAmount(BigDecimal itemAmount,String message) throws MeedlException {
        MeedlValidator.validateBigDecimalDataElement(itemAmount);
        if (itemAmount.compareTo(BigDecimal.ZERO) < 0) {
            log.info("{} --- {}",LoaneeLoanBreakdownMessages.AMOUNT_CANNOT_BE_LESS_THAN_ZERO.getMessage(),itemAmount);
            throw new MeedlException(message+" "+LoaneeLoanBreakdownMessages.AMOUNT_CANNOT_BE_LESS_THAN_ZERO.getMessage());
        }
    }

    public static void validateLoanBreakdowns(List<LoanBreakdown> loanBreakdowns) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanBreakdowns, LoanMessages.LOAN_BREAK_DOWN_CANNOT_BE_EMPTY.getMessage());
        for(LoanBreakdown loanBreakdown : loanBreakdowns){
            loanBreakdown.validate();
        }
    }

    public static void validateRCNumber(String rcNumber) throws MeedlException {
        MeedlValidator.validateDataElement(rcNumber, "Rc number is required.");
        boolean patternMatches = Pattern.compile(MeedlPatterns.RC_NUMBER_REGEX_PATTERN).matcher(rcNumber).matches();
        if (!patternMatches) {
            log.error("{} - {}", OrganizationMessages.INVALID_RC_NUMBER.getMessage(), rcNumber);
            throw new MeedlException(OrganizationMessages.INVALID_RC_NUMBER.getMessage());
        }
    }

    public static void validateTin(String tin) throws MeedlException {
        MeedlValidator.validateDataElement(tin, "Please provide TIN");
        boolean patternMatches = Pattern.compile(MeedlPatterns.TIN_REGEX_PATTERN).matcher(tin).matches();
        if (!patternMatches) {
            log.error("{} - {}", MeedlMessages.INVALID_TIN.getMessage(), tin);
            throw new MeedlException(MeedlMessages.INVALID_TIN.getMessage());
        }
    }

    public static void validateRate(Float rate, String message) throws MeedlException {
        if (rate == null) {
            throw new MeedlException(message);
        }
    }

    public static void validateAccountNumber(String bankAccountNumber, String message) throws MeedlException {
        if (StringUtils.isEmpty(bankAccountNumber)){
            throw new MeedlException(message);
        }
        if (bankAccountNumber.length() < 10 || bankAccountNumber.length() > 15){
            throw new MeedlException("Bank account number cannot be less than ten or greater than fifteen");
        }
    }

    public static List<String> validateNotificationListAndFilter(List<String> deleteNotificationList) throws MeedlException {
        if (CollectionUtils.isEmpty(deleteNotificationList)) {
            throw new MeedlException(MeedlNotificationMessages.NOTIFICATION_LIST_CANNOT_BE_EMPTY.getMessage());
        }

        List<String> validUUIDs = deleteNotificationList.stream()
                .filter(MeedlValidator::isValidUUID)
                .toList();

        if (validUUIDs.isEmpty()) {
            throw new MeedlException("No valid notification IDs found in the list.");
        }

        return validUUIDs;
    }


    private static boolean isValidUUID(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }
        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static void validateIncorrectStatus(Object status, Object[] values) throws MeedlException {
        if (status != null) {
            boolean isValid = Arrays.asList(values).contains(status);
            if (!isValid) {
                throw new MeedlException(
                        "Invalid status: " + status + ". Must be one of: " +
                                Arrays.toString(values)
                );
            }
        }
    }

    public static void validateActivationSatus(ActivationStatus activationStatus) throws MeedlException {
        if (!activationStatus.equals(ActivationStatus.APPROVED) && !activationStatus.equals(ActivationStatus.DECLINED)){
            throw new MeedlException(IdentityMessages.RESPONSE_FOR_INVITATION_CAN_EITHER_APPROVED_OR_DECLINED.getMessage());
        }
    }
}
