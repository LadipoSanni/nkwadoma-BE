package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import static africa.nkwadoma.nkwadoma.domain.constants.MiddlMessages.EMPTY_INPUT_FIELD_ERROR;
import static africa.nkwadoma.nkwadoma.domain.constants.MiddlMessages.INVALID_EMAIL_ADDRESS;

public class MiddleValidator {

    public static void validateEmail(String email) throws MiddlException {
        if (StringUtils.isEmpty(email) || !EmailValidator.getInstance().isValid(email.trim())) {
            throw new MiddlException(INVALID_EMAIL_ADDRESS.getMessage());
        }
    }

    public static void validateUserDataElement(String dataElement) throws MiddlException {
        if (StringUtils.isEmpty(dataElement)) {
            throw new MiddlException(EMPTY_INPUT_FIELD_ERROR.getMessage());
        }
    }

}
