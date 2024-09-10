package africa.nkwadoma.nkwadoma.domain.validation;


import africa.nkwadoma.nkwadoma.domain.exceptions.LearnSpaceUserException;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;

import static africa.nkwadoma.nkwadoma.domain.constants.UserConstants.INVALID_EMAIL;
import static africa.nkwadoma.nkwadoma.domain.constants.UserConstants.INVALID_FIRST_NAME;

public class UserValidator {
    public static void validateUserInput(UserIdentity user) throws LearnSpaceUserException {

        if (user.getEmail() == null) throwException(INVALID_EMAIL);
        if (user.getFirstName() == null) throwException(INVALID_FIRST_NAME);
//        if (user.getRole() == null) throwException(INVALID_ROLE);

    }
    private static void throwException(String message) throws LearnSpaceUserException {
//        log.error(message);
        throw new LearnSpaceUserException(message);
    }
}
