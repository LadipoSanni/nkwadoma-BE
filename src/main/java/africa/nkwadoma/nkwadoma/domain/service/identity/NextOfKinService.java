package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NextOfKinService implements CreateNextOfKinUseCase {
    private final NextOfKinOutputPort nextOfKinOutputPort;
    private final UserIdentityOutputPort userIdentityOutputPort;

    @Override
    public NextOfKin saveAdditionalDetails(NextOfKin nextOfKin) throws MeedlException {
        MeedlValidator.validateObjectInstance(nextOfKin, "Next of kin cannot be empty.");
        nextOfKin.validate();

        UserIdentity foundUserIdentity = userIdentityOutputPort.findById(nextOfKin.getUserIdentity().getId());
        Optional<NextOfKin> foundNextOfKin = nextOfKinOutputPort.findByUserId(foundUserIdentity.getId());
        if (foundNextOfKin.isPresent()) {
            throw new MeedlException(IdentityMessages.USER_HAS_NEXT_OF_KIN.getMessage());
        }
        nextOfKin.setUserIdentity(foundUserIdentity);
        NextOfKin savedNextOfKin = nextOfKinOutputPort.save(nextOfKin);
        log.info("Saved next of kin: {}", savedNextOfKin);
        updateUserNextOfKinDetails(foundUserIdentity, savedNextOfKin);
        return savedNextOfKin;
    }

    private void updateUserNextOfKinDetails(UserIdentity userIdentity, NextOfKin savedNextOfKin) throws MeedlException {
        log.info("Updating next of kin for user : {}", userIdentity);
        userIdentity.setNextOfKin(savedNextOfKin);
        log.info("Next of kin before being updated on user db {}", userIdentity.getNextOfKin());
        userIdentityOutputPort.save(userIdentity);
    }

}
