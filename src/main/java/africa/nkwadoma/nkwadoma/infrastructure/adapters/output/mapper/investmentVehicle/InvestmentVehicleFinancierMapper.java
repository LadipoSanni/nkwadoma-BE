package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.FinancierEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleFinancierEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = FinancierMapper.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InvestmentVehicleFinancierMapper {

    InvestmentVehicleFinancierEntity toInvestmentVehicleFinancierEntity(InvestmentVehicleFinancier investmentVehicleFinancier);


    InvestmentVehicleFinancier toInvestmentVehicleFinancier(InvestmentVehicleFinancierEntity investmentVehicleFinancierEntity);

}
