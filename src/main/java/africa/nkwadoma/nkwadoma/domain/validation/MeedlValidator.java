package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.glassfish.jaxb.core.v2.*;
import org.hibernate.validator.internal.constraintvalidators.hv.*;

import java.math.BigDecimal;
import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages.*;

@Slf4j
public class MeedlValidator {

    public static void validateEmail(String email) throws MeedlException {
        if (isEmptyString(email) || !EmailValidator.getInstance().isValid(email.trim())) {
            throw new MeedlException(MeedlMessages.INVALID_EMAIL_ADDRESS.getMessage());
        }
    }

    public static void validateUUID(String dataElement) throws MeedlException {
        //TODO
        try {
            UUID.fromString(dataElement);
        } catch (IllegalArgumentException e) {
            throw new MeedlException(UUID_NOT_VALID.getMessage());
        }
    }
    public static void validateDataElement(String dataElement) throws MeedlException {
        if (isEmptyString(dataElement)) {
            throw new MeedlException(MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage());
        }

    }

    private static boolean isEmptyString(String dataElement) {
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
}
