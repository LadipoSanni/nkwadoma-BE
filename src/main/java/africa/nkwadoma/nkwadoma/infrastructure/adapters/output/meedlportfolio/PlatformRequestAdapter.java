package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.meedlportfolio;

import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.PlatformRequestOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.PlatformRequest;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.meedlPortfolio.PlatformRequestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlnotification.PlatformRequestEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.meedlnotification.PlatformRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlatformRequestAdapter implements PlatformRequestOutputPort {
    private final PlatformRequestRepository platformRequestRepository;
    private final PlatformRequestMapper platformRequestMapper;

    @Override
    public PlatformRequest save(PlatformRequest platformRequest) throws MeedlException {
        MeedlValidator.validateObjectInstance(platformRequest, "Platform request cannot be empty");
        platformRequest.validateObligorLoanLimitData();

        PlatformRequestEntity platformRequestEntity = platformRequestMapper.map(platformRequest);
        platformRequestEntity = platformRequestRepository.save(platformRequestEntity);
        return platformRequestMapper.map(platformRequestEntity);

    }
}
