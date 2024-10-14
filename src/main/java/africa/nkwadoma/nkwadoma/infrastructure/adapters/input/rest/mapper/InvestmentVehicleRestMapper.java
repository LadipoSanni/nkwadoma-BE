package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper;


import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.CreateInvestmentVehicleRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.UpdateInvestmentVehicleRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.CreateInvestmentVehicleResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.UpdateInvestmentVehicleResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.ViewInvestmentVehicleResponse;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface InvestmentVehicleRestMapper {
    InvestmentVehicle toInvestmentVehicle(CreateInvestmentVehicleRequest investmentVehicleRequest);

    CreateInvestmentVehicleResponse toCreateInvestmentVehicleResponse(InvestmentVehicle investmentVehicle);

    InvestmentVehicle mapUpdateInvestmentVehicleRequestToInvestmentVehicle(UpdateInvestmentVehicleRequest investmentVehicleRequest);

    UpdateInvestmentVehicleResponse toUpdateInvestmentVehicleResponse(InvestmentVehicle investmentVehicle);

    List<ViewInvestmentVehicleResponse> toViewAllInvestmentVehicleResponse(List<InvestmentVehicle> investmentVehicleIdentities);
}
