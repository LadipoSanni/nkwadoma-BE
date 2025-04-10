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
    private final NextOfKinIdentityOutputPort nextOfKinIdentityOutputPort;
    private final LoaneeOutputPort loaneeOutputPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final UserIdentityMapper userIdentityMapper;

    @Override
    public NextOfKin saveAdditionalDetails(NextOfKin nextOfKin) throws MeedlException {
        MeedlValidator.validateObjectInstance(nextOfKin);
        nextOfKin.validate();
        trimSpaceForUserIdentity(nextOfKin.getLoanee());
        Loanee foundLoanee = loaneeOutputPort.findByUserId(nextOfKin.getLoanee().getUserIdentity().getId()).
                orElseThrow(()-> new MeedlException(IdentityMessages.LOANEE_NOT_FOUND.getMessage()));
        Optional<NextOfKin> foundNextOfKin = nextOfKinIdentityOutputPort.findByLoaneeId(foundLoanee.getId());
        if (foundNextOfKin.isPresent()) {
            throw new MeedlException(IdentityMessages.LOANEE_HAS_NEXT_OF_KIN.getMessage());
        }
        foundLoanee = updateLoanee(nextOfKin, foundLoanee);
        nextOfKin.setLoanee(foundLoanee);
        return nextOfKinIdentityOutputPort.save(nextOfKin);
    }

    private Loanee updateLoanee(NextOfKin nextOfKin, Loanee foundLoanee) throws MeedlException {
        log.info("User identity before mapping updating additional details {}", foundLoanee.getUserIdentity());
        boolean isIdentityVerified = foundLoanee.getUserIdentity().isIdentityVerified();
        UserIdentity userIdentity = userIdentityMapper.updateUser(nextOfKin.getLoanee().getUserIdentity(), foundLoanee.getUserIdentity());
        userIdentity.setIdentityVerified(isIdentityVerified);
        log.info("User identity after mapping additional details {}", foundLoanee.getUserIdentity());
        userIdentity = userIdentityOutputPort.save(userIdentity);
        log.info("Updated User identity: {}", userIdentity);
        foundLoanee.setUserIdentity(userIdentity);
        foundLoanee = loaneeOutputPort.save(foundLoanee);
        return foundLoanee;
    }

    private void trimSpaceForUserIdentity(Loanee loanee) {
        if (ObjectUtils.isNotEmpty(loanee)) {
            loanee.getUserIdentity().setAlternateContactAddress(loanee.getUserIdentity().getAlternateContactAddress().trim());
            loanee.getUserIdentity().setAlternatePhoneNumber(loanee.getUserIdentity().getAlternatePhoneNumber().trim());
            loanee.getUserIdentity().setAlternateEmail(loanee.getUserIdentity().getAlternateEmail().trim());
        }
    }
}
