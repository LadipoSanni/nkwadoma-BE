package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.invesmentVehicle;


import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.InvestmentVehicleOperationStatusRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.SetUpInvestmentVehicleRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.UpdateInvestmentVehicleRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.InvestmentVehicleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface InvestmentVehicleRestMapper {
    InvestmentVehicle toInvestmentVehicle(SetUpInvestmentVehicleRequest investmentVehicleRequest);

    @Mapping( target= "fundRaisingStatus",source = "vehicleOperation.fundRaisingStatus")
    @Mapping(target = "deployingStatus", source = "vehicleOperation.deployingStatus")
    InvestmentVehicleResponse toInvestmentVehicleResponse(InvestmentVehicle investmentVehicle);

    InvestmentVehicle mapUpdateInvestmentVehicleRequestToInvestmentVehicle(UpdateInvestmentVehicleRequest investmentVehicleRequest);

    List<InvestmentVehicleResponse> toViewAllInvestmentVehicleResponse(List<InvestmentVehicle> investmentVehicleIdentities);

    @Mapping( target= "vehicleOperation.fundRaisingStatus",source = "fundRaising")
    @Mapping(target = "vehicleOperation.deployingStatus", source = "deployingStatus")
    @Mapping(target = "vehicleOperation.couponDistributionStatus", source = "couponDistributionStatus")
    @Mapping(target = "id", source = "investmentVehicleId")
    InvestmentVehicle mapInvestmentVehicleOperationStatusToVehicleOperationStatus(InvestmentVehicleOperationStatusRequest vehicleOperationStatus);
}
