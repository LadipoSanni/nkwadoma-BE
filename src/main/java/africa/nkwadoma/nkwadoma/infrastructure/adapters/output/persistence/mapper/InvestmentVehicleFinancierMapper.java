package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleFinancierEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

public interface InvestmentVehicleFinancierMapper {

    InvestmentVehicleFinancierEntity toInvestmentVehicleFinancialEntity(InvestmentVehicleFinancier investmentVehicleFinancier);


    InvestmentVehicleFinancier toInvestmentVehicleFinancial(InvestmentVehicleFinancierEntity investmentVehicleFinancierEntity);

}
