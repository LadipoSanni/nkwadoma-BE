package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class NextOfKinIdentityAdapter implements NextOfKinIdentityOutputPort {
    private final NextOfKinRepository nextOfKinRepository;
    private final LoaneeOutputPort loaneeOutputPort;
    private final NextOfKinMapper nextOfKinMapper;

    @Override
    public NextOfKin save(NextOfKin nextOfKin) {
        NextOfKinEntity nextOfKinEntity = nextOfKinMapper.toNextOfKinEntity(nextOfKin);

        NextOfKinEntity savedNextOfKinEntity = nextOfKinRepository.save(nextOfKinEntity);
        return nextOfKinMapper.toNextOfKin(savedNextOfKinEntity);
    }
}
