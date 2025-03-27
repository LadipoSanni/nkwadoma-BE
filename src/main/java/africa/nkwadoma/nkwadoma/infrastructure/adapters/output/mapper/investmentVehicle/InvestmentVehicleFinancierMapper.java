package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.FinancierVehicleDetail;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.FinancierInvestmentDetailResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleFinancierEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses = FinancierMapper.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InvestmentVehicleFinancierMapper {

    InvestmentVehicleFinancierEntity toInvestmentVehicleFinancierEntity(InvestmentVehicleFinancier investmentVehicleFinancier);

    InvestmentVehicleFinancier toInvestmentVehicleFinancier(InvestmentVehicleFinancierEntity investmentVehicleFinancierEntity);

    List<InvestmentVehicleFinancier> toInvestmentVehicleFinancier(List<InvestmentVehicleFinancierEntity> investmentVehicleFinancierEntity);


}
