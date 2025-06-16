package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentvehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.CapitalDistributionOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.CapitalDistribution;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentvehicle.CapitalDistributionMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle.CapitalDistributionEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentvehicle.CapitalDistributionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class CapitalDistributionAdapter implements CapitalDistributionOutputPort {

    private final CapitalDistributionMapper capitalDistributionMapper;
    private final CapitalDistributionRepository capitalDistributionRepository;

    @Override
    public CapitalDistribution save(CapitalDistribution capitalDistribution) throws MeedlException {
        MeedlValidator.validateObjectInstance(capitalDistribution,"Capital distribution cannot be empty");
        CapitalDistributionEntity capitalDistributionEntity =
                capitalDistributionMapper.toCapitalDistributionEntity(capitalDistribution);
        capitalDistributionEntity = capitalDistributionRepository.save(capitalDistributionEntity);
        return capitalDistributionMapper.toCapitalDistribution(capitalDistributionEntity);
    }

    @Override
    public void deleteById(String capitalId) throws MeedlException {
        MeedlValidator.validateUUID(capitalId,"Capital distribution id cannot be empty");
        capitalDistributionRepository.deleteById(capitalId);
    }
}
