package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper;


import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.CreateInvestmentVehicleRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.CreateInvestmentVehicleResponse;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface InvestmentVehicleMapper {
    InvestmentVehicleIdentity toInvestmentVehicleIdentity(CreateInvestmentVehicleRequest investmentVehicleRequest);

    CreateInvestmentVehicleResponse toCreateInvestmentVehicleResponse(InvestmentVehicleIdentity investmentVehicleIdentity);

}
