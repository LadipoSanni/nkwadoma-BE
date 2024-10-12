package africa.nkwadoma.nkwadoma.domain.validation;


import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.UserConstants.INVALID_EMAIL;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.UserConstants.INVALID_FIRST_NAME;

public class UserValidator {
    public static void validateUserInput(UserIdentity user) throws MeedlException {

        if (user.getEmail() == null) throwException(INVALID_EMAIL.getMessage());
        if (user.getFirstName() == null) throwException(INVALID_FIRST_NAME.getMessage());
//        if (user.getRole() == null) throwException(INVALID_ROLE);

    }
    private static void throwException(String message) throws MeedlException {
//        log.error(message);
        throw new MeedlException(message);
    }


}
