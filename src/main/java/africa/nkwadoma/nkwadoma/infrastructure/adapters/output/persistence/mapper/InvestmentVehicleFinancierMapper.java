package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.FinancierEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

public interface InvestmentVehicleFinancierMapper {

    FinancierEntity toInvestmentVehicleFinancialEntity(Financier investmentVehicleFinancier);


    Financier toInvestmentVehicleFinancial(FinancierEntity investmentVehicleFinancierEntity);

}
