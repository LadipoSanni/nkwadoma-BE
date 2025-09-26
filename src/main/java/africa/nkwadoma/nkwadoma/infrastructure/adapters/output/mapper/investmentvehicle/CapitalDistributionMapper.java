package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentvehicle;

import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.CapitalDistribution;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.NextOfKinMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle.CapitalDistributionEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.UserIdentityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {UserIdentityMapper.class, NextOfKinMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CapitalDistributionMapper {
    CapitalDistributionEntity toCapitalDistributionEntity(CapitalDistribution capitalDistribution);

    CapitalDistribution toCapitalDistribution(CapitalDistributionEntity capitalDistributionEntity);
}
