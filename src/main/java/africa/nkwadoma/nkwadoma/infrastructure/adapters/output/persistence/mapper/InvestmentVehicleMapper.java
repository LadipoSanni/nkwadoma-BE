package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleEntity;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface InvestmentVehicleMapper {


    InvestmentVehicleEntity toInvestmentVehicleEntity(InvestmentVehicle investmentVehicle);
    InvestmentVehicle toInvestmentVehicleIdentity(InvestmentVehicleEntity investmentEntity);


}
