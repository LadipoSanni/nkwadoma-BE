package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.glassfish.jaxb.core.v2.*;
import org.hibernate.validator.internal.constraintvalidators.hv.*;

import java.math.*;
import java.util.*;
import java.util.regex.Pattern;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.PASSWORD_PATTERN;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.WEAK_PASSWORD;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages.*;

@Slf4j
public class MeedlValidator {

    public static void validateEmail(String email) throws MeedlException {
        if (isEmptyString(email) || !EmailValidator.getInstance().isValid(email.trim())) {
            throw new MeedlException(MeedlMessages.INVALID_EMAIL_ADDRESS.getMessage());
        }
    }

    public static void validateUUID(String dataElement) throws MeedlException {
        log.info("validateUUID {}", dataElement);
        validateDataElement(dataElement);
        try {
            UUID.fromString(dataElement);
        } catch (IllegalArgumentException e) {
            log.info("{}. The invalid UUID {}", e.getMessage(), dataElement);
            throw new MeedlException(UUID_NOT_VALID.getMessage());
        }
    }
    public static void validateDataElement(String dataElement) throws MeedlException {
        if (isEmptyString(dataElement)) {
            throw new MeedlException(MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage());
        }
    }

    public static boolean isEmptyString(String dataElement) {
        return StringUtils.isEmpty(dataElement) || StringUtils.isBlank(dataElement);
    }

    public static void validateBigDecimalDataElement(BigDecimal dataElement) throws MeedlException {
        if (dataElement == null){
            throw new MeedlException(MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage());
        }
    }

    public static void validateFloatDataElement(Float dataElement) throws MeedlException {
        if (dataElement == null){
            throw new MeedlException(MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage());
        }
    }
    public static void validateIntegerDataElement(int dataElement) throws MeedlException {
        if (dataElement == 0){
            throw new MeedlException(MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage());
        }
    }

    public static void validateObjectInstance(Object instance) throws MeedlException {
        if (ObjectUtils.isEmpty(instance)){
            throw new MeedlException(MeedlMessages.INVALID_OBJECT.getMessage());
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

    public static void validateDoubleDataElement(Double dataElement) throws MeedlException {
        if (dataElement == null){
            throw new MeedlException(MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage());
        }
    }
    public static void validatePassword(String password) throws MeedlException {
        validateDataElement(password);
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN.getMessage());
        if (!pattern.matcher(password).matches()) {
            throw new IdentityException(WEAK_PASSWORD.getMessage());
        }
    }
}
