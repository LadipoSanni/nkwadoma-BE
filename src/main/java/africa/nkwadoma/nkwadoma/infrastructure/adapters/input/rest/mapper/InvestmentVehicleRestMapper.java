package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper;


import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.CreateInvestmentVehicleRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.UpdateInvestmentVehicleRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.CreateInvestmentVehicleResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.UpdateInvestmentVehicleResponse;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface InvestmentVehicleRestMapper {
    InvestmentVehicle toInvestmentVehicle(CreateInvestmentVehicleRequest investmentVehicleRequest);

    CreateInvestmentVehicleResponse toCreateInvestmentVehicleResponse(InvestmentVehicle investmentVehicle);

    InvestmentVehicle updateInvestmentVehicleRequestToInvestmentVehicle(UpdateInvestmentVehicleRequest investmentVehicleRequest);

    UpdateInvestmentVehicleResponse investmentVehicleToUpdateInvestmentVehicleResponse(InvestmentVehicle investmentVehicle);
}
