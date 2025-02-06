package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoaneeLoanBreakdownException;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.math.*;
import java.util.*;
import java.util.regex.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.*;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages.*;

@Slf4j
public class MeedlValidator {

    public static void validateEmail(String email) throws MeedlException {
        if (isEmptyString(email) || !EmailValidator.getInstance().isValid(email.trim())) {
            log.info("Invalid email address provided : {}", email);
            throw new MeedlException(MeedlMessages.INVALID_EMAIL_ADDRESS.getMessage());
        }
    }

    public static void validateLoanDecision(String loanReferralStatus) throws LoanException {
        boolean matches = Pattern.matches("^(ACCEPTED|DECLINED)$", loanReferralStatus);
        if (!matches) {
            throw new LoanException(LoanMessages.INVALID_LOAN_DECISION.getMessage());
        }
    }

    public static void validateUUID(String dataElement) throws MeedlException {
        log.info("validateUUID {}", dataElement);
        validateDataElement(dataElement,"An empty id can not be used to perform this operation");
        try {
            UUID.fromString(dataElement);
        } catch (IllegalArgumentException e) {
            log.info("{}. The invalid UUID {}", e.getMessage(), dataElement);
            throw new MeedlException(UUID_NOT_VALID.getMessage());
        }
    }
    public static void validateUUID(String dataElement, String message) throws MeedlException {
        log.info("validateUUID {}", dataElement);
        validateDataElement(dataElement, message.concat(StringUtils.SPACE).concat(MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage()));
        try {
            UUID.fromString(dataElement);
        } catch (IllegalArgumentException e) {
            log.info("{}. The invalid UUID : {} : {}", e.getMessage(), dataElement, message);
            throw new MeedlException(message);
        }
    }

    public static void validateDataElement(String dataElement, String message) throws MeedlException {
        if (isEmptyString(dataElement)) {
            log.error(message);
            throw new MeedlException(message);
        }
    }

    public static boolean isEmptyString(String dataElement) {
        return StringUtils.isEmpty(dataElement) || StringUtils.isBlank(dataElement);
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

    public static void validateObjectInstance(Object instance) throws MeedlException {
        if (ObjectUtils.isEmpty(instance)) {
            throw new MeedlException(MeedlMessages.INVALID_OBJECT.getMessage());
        }
    }

    public static void validateObjectInstance(Object instance, String message) throws MeedlException {
        if (ObjectUtils.isEmpty(instance)){
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
    public static void validateBvn(String bvn) throws MeedlException {
        MeedlValidator.validateDataElement(bvn, "bvn is empty");
        String regex = "^\\d{11}$";

        boolean isValid = Pattern.matches(regex, bvn);
        if (!isValid) {
            log.error("Invalid bvn {}", bvn);
            throw new MeedlException("Invalid bvn provided");
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
            throw new IdentityException(WEAK_PASSWORD.getMessage());
        }
    }
    public static void validateObjectName(String name,  String message) throws MeedlException {
        MeedlValidator.validateDataElement(name, message);
        String regex =  "^(?=.*[A-Za-z])(?=.*['A-Za-z])[A-Za-z0-9' _-]+$";
        Pattern pattern = Pattern.compile(regex);
        boolean isValid = pattern.matcher(name).matches();
        if (!isValid){
            log.error("Invalid name pattern: {}", name);
            throw new MeedlException("Name must include letters and can only contain letters, numbers, spaces - _ '");
        }
    }
    public static void validateEmailDomain(String inviteeEmail, String inviterEmail) throws MeedlException {
        MeedlValidator.validateEmail(inviteeEmail);
        MeedlValidator.validateEmail(inviterEmail);
        if (!compareEmailDomain(inviteeEmail,inviterEmail)){
            log.error("{} - {} : {}",DOMAIN_EMAIL_DOES_NOT_MATCH.getMessage(), inviteeEmail, inviterEmail);
            throw new IdentityException(DOMAIN_EMAIL_DOES_NOT_MATCH.getMessage());
        }
    }
    private static boolean compareEmailDomain(String inviteeEmail, String inviterEmail) {
        String inviteeEmailDomain =
                inviteeEmail.substring(inviteeEmail.indexOf(EMAIL_INDEX.getMessage()));
        String inviterEmailDomain = inviterEmail.substring(inviterEmail.indexOf(EMAIL_INDEX.getMessage()));
        return StringUtils.equals(inviterEmailDomain, inviteeEmailDomain);
    }
    public static void validateOrganizationUserIdentities(List<OrganizationEmployeeIdentity> userIdentities) throws MeedlException {
        log.info("Started validating for user identities (List) : {}", userIdentities);
        log.info("validating to check for empty list : {}", CollectionUtils.isEmpty(userIdentities));
        if (CollectionUtils.isEmpty(userIdentities)){
            log.error("{} - {}", USER_IDENTITY_CANNOT_BE_NULL.getMessage(), userIdentities);
            throw new IdentityException(USER_IDENTITY_CANNOT_BE_NULL.getMessage());
        }
        for(OrganizationEmployeeIdentity userIdentity : userIdentities){
            MeedlValidator.validateObjectInstance(userIdentity);
            userIdentity.getMeedlUser().validate();
        }
        log.info("Users identity validation completed... for user {} ", userIdentities);
    }

    public static void validateLoanRequest(LoanRequest foundLoanRequest) throws LoanException {
        if (ObjectUtils.isEmpty(foundLoanRequest)){
            log.info("Loan request: {}", foundLoanRequest);
            throw new LoanException(LoanMessages.LOAN_REQUEST_NOT_FOUND.getMessage());
        }
    }

    public static void validateNegativeAmount(BigDecimal itemAmount,String message) throws MeedlException {
        MeedlValidator.validateBigDecimalDataElement(itemAmount);
        if (itemAmount.compareTo(BigDecimal.ZERO) < 0) {
            log.info("{} --- {}",LoaneeLoanBreakdownMessages.AMOUNT_CANNOT_BE_LESS_THAN_ZERO.getMessage(),itemAmount);
            throw new LoaneeLoanBreakdownException(message+" "+LoaneeLoanBreakdownMessages.AMOUNT_CANNOT_BE_LESS_THAN_ZERO.getMessage());
        }
    }

    public static void validateLoanBreakdowns(List<LoanBreakdown> loanBreakdowns) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanBreakdowns);
        for(LoanBreakdown loanBreakdown : loanBreakdowns){
            loanBreakdown.validate();
        }
    }

    public static void validateRCNumber(String rcNumber) throws MeedlException {
        boolean patternMatches = Pattern.compile(MeedlPatterns.RC_NUMBER_REGEX_PATTERN).matcher(rcNumber).matches();
        if (!patternMatches) {
            log.error("{} - {}", OrganizationMessages.INVALID_RC_NUMBER.getMessage(), rcNumber);
            throw new MeedlException(OrganizationMessages.INVALID_RC_NUMBER.getMessage());
        }
    }

    public static void validateTin(String tin) throws MeedlException {
        boolean patternMatches = Pattern.compile(MeedlPatterns.TIN_REGEX_PATTERN).matcher(tin).matches();
        if (!patternMatches) {
            log.error("{} - {}", MeedlMessages.INVALID_TIN.getMessage(), tin);
            throw new MeedlException(MeedlMessages.INVALID_TIN.getMessage());
        }
    }
}
