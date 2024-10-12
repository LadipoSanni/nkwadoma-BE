package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.math.BigDecimal;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages.EMPTY_INPUT_FIELD_ERROR;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages.INVALID_EMAIL_ADDRESS;

public class MeedlValidator {

    public static void validateEmail(String email) throws MeedlException {
        if (StringUtils.isEmpty(email) || !EmailValidator.getInstance().isValid(email.trim())) {
            throw new MeedlException(INVALID_EMAIL_ADDRESS.getMessage());
        }
    }

    public static void validateUUID(String UUID) throws MeedlException {
        //TODO
    }
    public static void validateDataElement(String dataElement) throws MeedlException {
        if (StringUtils.isEmpty(dataElement) || StringUtils.isBlank(dataElement)) {
            throw new MeedlException(EMPTY_INPUT_FIELD_ERROR.getMessage());
        }

    }

    public static void validateBigDecimalDataElement(BigDecimal dataElement) throws MeedlException {
        if (dataElement == null){
            throw new MeedlException(EMPTY_INPUT_FIELD_ERROR.getMessage());
        }
    }

    public static void validateFloatDataElement(Float dataElement) throws MeedlException {
        if (dataElement == null){
            throw new MeedlException(EMPTY_INPUT_FIELD_ERROR.getMessage());
        }
    }
}
