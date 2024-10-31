package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper;


import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.CreateInvestmentVehicleRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.UpdateInvestmentVehicleRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.InvestmentVehicleResponse;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface InvestmentVehicleRestMapper {
    InvestmentVehicle toInvestmentVehicle(CreateInvestmentVehicleRequest investmentVehicleRequest);

    InvestmentVehicleResponse toInvestmentVehicleResponse(InvestmentVehicle investmentVehicle);

    InvestmentVehicle mapUpdateInvestmentVehicleRequestToInvestmentVehicle(UpdateInvestmentVehicleRequest investmentVehicleRequest);

    List<InvestmentVehicleResponse> toViewAllInvestmentVehicleResponse(List<InvestmentVehicle> investmentVehicleIdentities);
}
