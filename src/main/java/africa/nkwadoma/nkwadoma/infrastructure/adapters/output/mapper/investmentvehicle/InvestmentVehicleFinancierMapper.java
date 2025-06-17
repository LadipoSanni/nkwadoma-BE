package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentvehicle;

import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier.FinancierMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle.InvestmentVehicleFinancierEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses ={FinancierMapper.class, InvestmentVehicleMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InvestmentVehicleFinancierMapper {

    InvestmentVehicleFinancierEntity toInvestmentVehicleFinancierEntity(InvestmentVehicleFinancier investmentVehicleFinancier);

    InvestmentVehicleFinancier toInvestmentVehicleFinancier(InvestmentVehicleFinancierEntity investmentVehicleFinancierEntity);

    List<InvestmentVehicleFinancier> toInvestmentVehicleFinancier(List<InvestmentVehicleFinancierEntity> investmentVehicleFinancierEntity);

}
