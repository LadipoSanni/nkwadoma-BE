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
    @Mapping(target = "interestRateOffered", source = "rate")
    InvestmentVehicle toInvestmentVehicle(SetUpInvestmentVehicleRequest investmentVehicleRequest);

    @Mapping( target= "fundRaisingStatus",source = "vehicleOperation.fundRaisingStatus")
    @Mapping(target = "deployingStatus", source = "vehicleOperation.deployingStatus")
    @Mapping(target = "couponDistributionStatus", source = "vehicleOperation.couponDistributionStatus")
    @Mapping(target = "recollectionStatus", source = "vehicleClosureStatus.recollectionStatus")
    @Mapping(target = "maturity", source = "vehicleClosureStatus.maturity")
    @Mapping(target = "rate", source = "interestRateOffered")
    @Mapping(target = "amountFinancierInvested", source = "amountFinancierInvested")
    @Mapping(target = "investmentVehicleDesignation", source = "investmentVehicleDesignation")
    InvestmentVehicleResponse toInvestmentVehicleResponse(InvestmentVehicle investmentVehicle);

    InvestmentVehicle mapUpdateInvestmentVehicleRequestToInvestmentVehicle(UpdateInvestmentVehicleRequest investmentVehicleRequest);

    List<InvestmentVehicleResponse> toViewAllInvestmentVehicleResponse(List<InvestmentVehicle> investmentVehicleIdentities);



    @Mapping( target= "vehicleOperation.fundRaisingStatus",source = "fundRaising")
    @Mapping(target = "vehicleOperation.deployingStatus", source = "deployingStatus")
    @Mapping( target= "vehicleOperation.couponDistributionStatus",source = "couponDistributionStatus")
    @Mapping(target = "vehicleClosureStatus.recollectionStatus", source = "recollectionStatus")
    @Mapping(target = "vehicleClosureStatus.maturity", source = "maturity")
    @Mapping(target = "id", source = "investmentVehicleId")
    InvestmentVehicle mapInvestmentVehicleOperationStatusToVehicleOperationStatus(InvestmentVehicleOperationStatusRequest vehicleOperationStatus);
}
