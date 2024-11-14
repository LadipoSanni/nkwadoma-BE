package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import lombok.*;
import org.apache.commons.lang3.*;
import org.springframework.stereotype.*;

@Service
@RequiredArgsConstructor
public class NextOfKinService implements CreateNextOfKinUseCase {
    private final NextOfKinIdentityOutputPort nextOfKinIdentityOutputPort;

    @Override
    public NextOfKin createNextOfKin(NextOfKin nextOfKin) throws MeedlException {
        MeedlValidator.validateObjectInstance(nextOfKin);
        nextOfKin.validate();
        trimSpaceForUserIdentity(nextOfKin.getLoanee());
        return nextOfKinIdentityOutputPort.save(nextOfKin);
    }

    private void trimSpaceForUserIdentity(Loanee loanee) {
        if (ObjectUtils.isNotEmpty(loanee)) {
            loanee.getUserIdentity().setAlternateContactAddress(loanee.getUserIdentity().getAlternateContactAddress().trim());
            loanee.getUserIdentity().setAlternatePhoneNumber(loanee.getUserIdentity().getAlternatePhoneNumber().trim());
            loanee.getUserIdentity().setAlternateEmail(loanee.getUserIdentity().getAlternateEmail().trim());
        }
    }
}
