package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NextOfKinService implements NextOfKinUseCase {
    private final NextOfKinOutputPort nextOfKinOutputPort;
    private final UserIdentityOutputPort userIdentityOutputPort;

    @Override
    public NextOfKin saveAdditionalDetails(NextOfKin nextOfKin) throws MeedlException {
        MeedlValidator.validateObjectInstance(nextOfKin, "Next of kin cannot be empty.");
        nextOfKin.validate();

        UserIdentity foundUserIdentity = userIdentityOutputPort.findById(nextOfKin.getUserId());
        NextOfKin foundNextOfKin = foundUserIdentity.getNextOfKin();
        log.info("Found user in add additional details-next of kin :{} ", foundUserIdentity);
        if (ObjectUtils.isNotEmpty(foundNextOfKin)) {
            log.error("User has next of kin {}", foundNextOfKin);
            throw new IdentityException(IdentityMessages.USER_HAS_NEXT_OF_KIN.getMessage());
        }
        NextOfKin savedNextOfKin = nextOfKinOutputPort.save(nextOfKin);
        log.info("Saved next of kin: {}", savedNextOfKin);
        updateUserNextOfKinDetails(foundUserIdentity, savedNextOfKin, nextOfKin);
        return savedNextOfKin;
    }

    private void updateAlternativeDetails(UserIdentity userIdentity, NextOfKin nextOfKin) throws MeedlException {
        userIdentity.setAlternateEmail(nextOfKin.getAlternateEmail());
        userIdentity.setAlternatePhoneNumber(nextOfKin.getAlternatePhoneNumber());
        userIdentity.setAlternateContactAddress(nextOfKin.getAlternateContactAddress());
        userIdentityOutputPort.save(userIdentity);
    }

    private void updateUserNextOfKinDetails(UserIdentity userIdentity, NextOfKin savedNextOfKin, NextOfKin nextOfKin) throws MeedlException {
        log.info("Updating next of kin for user : {}", userIdentity);
        userIdentity.setNextOfKin(savedNextOfKin);
        updateAlternativeDetails(userIdentity, nextOfKin);
        log.info("Next of kin before being updated on user db {}", userIdentity.getNextOfKin());
        userIdentityOutputPort.save(userIdentity);
    }
//    private Loanee updateLoanee(NextOfKin nextOfKin, Loanee foundLoanee) throws MeedlException {
//        log.info("User identity before mapping updating additional details {}", foundLoanee.getUserIdentity());
//        boolean isIdentityVerified = foundLoanee.getUserIdentity().isIdentityVerified();
//        UserIdentity userIdentity = userIdentityMapper.updateUser(nextOfKin.getLoanee().getUserIdentity(), foundLoanee.getUserIdentity());
//        userIdentity.setIdentityVerified(isIdentityVerified);
//        log.info("User identity after mapping additional details {}", foundLoanee.getUserIdentity());
//        userIdentity = userIdentityOutputPort.save(userIdentity);
//        log.info("Updated User identity: {}", userIdentity);
//        foundLoanee.setUserIdentity(userIdentity);
//        foundLoanee = loaneeOutputPort.save(foundLoanee);
//        return foundLoanee;
//    }

}
