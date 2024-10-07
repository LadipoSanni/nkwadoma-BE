package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleEntity;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface InvestmentVehicleIdentityMapper {


    InvestmentVehicleEntity toInvestmentVehicleEntity(InvestmentVehicleIdentity investmentVehicleIdentity);
    InvestmentVehicleIdentity toInvestmentVehicleIdentity(InvestmentVehicleEntity investmentEntity);


}
