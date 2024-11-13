package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import lombok.*;
import org.springframework.stereotype.*;

@Service
@RequiredArgsConstructor
public class NextOfKinService implements CreateNextOfKinUseCase {
    private final NextOfKinIdentityOutputPort nextOfKinIdentityOutputPort;

    @Override
    public NextOfKin createNextOfKin(NextOfKin nextOfKin) throws MeedlException {
        MeedlValidator.validateObjectInstance(nextOfKin);
        nextOfKin.validate();
        nextOfKin.trimSpaceForUserIdentity(nextOfKin.getLoanee());
        return nextOfKinIdentityOutputPort.save(nextOfKin);
    }
}
