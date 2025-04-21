package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleFinancierEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses ={FinancierMapper.class, InvestmentVehicleMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InvestmentVehicleFinancierMapper {

    InvestmentVehicleFinancierEntity toInvestmentVehicleFinancierEntity(InvestmentVehicleFinancier investmentVehicleFinancier);

    @Mapping(source = "investmentVehicle", target = "investmentVehicle")
    InvestmentVehicleFinancier toInvestmentVehicleFinancier(InvestmentVehicleFinancierEntity investmentVehicleFinancierEntity);

    List<InvestmentVehicleFinancier> toInvestmentVehicleFinancier(List<InvestmentVehicleFinancierEntity> investmentVehicleFinancierEntity);

}
