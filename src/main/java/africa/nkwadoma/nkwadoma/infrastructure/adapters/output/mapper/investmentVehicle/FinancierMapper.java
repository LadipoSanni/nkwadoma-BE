package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.NextOfKinMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.FinancierEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.UserIdentityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import javax.xml.transform.Source;

@Mapper(componentModel = "spring", uses ={UserIdentityMapper.class, NextOfKinMapper.class }, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinancierMapper {
    FinancierEntity map(Financier financier);

    @Mapping(target = "percentageOwnershipOrShare", source = "percentageOwnershipOrShare")
    Financier map(FinancierEntity financierEntity);
}
