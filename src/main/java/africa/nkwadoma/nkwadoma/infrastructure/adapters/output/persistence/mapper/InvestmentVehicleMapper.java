package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleEntity;
import org.mapstruct.*;


@Mapper(componentModel = "spring"  , uses = InvestmentVehicleFinancierMapper.class,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InvestmentVehicleMapper {


    InvestmentVehicleEntity toInvestmentVehicleEntity(InvestmentVehicle investmentVehicle);
    InvestmentVehicle toInvestmentVehicle(InvestmentVehicleEntity investmentEntity);


}
