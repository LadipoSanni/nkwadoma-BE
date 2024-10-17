package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleFinancierEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface InvestmentVehicleFinancierMapper {

    InvestmentVehicleFinancierEntity toInvestmentVehicleFinancialEntity(InvestmentVehicleFinancier investmentVehicleFinancier);


    InvestmentVehicleFinancier toInvestmentVehicleFinancial(InvestmentVehicleFinancierEntity investmentVehicleFinancierEntity);

}
