package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.invesmentvehicle;

import africa.nkwadoma.nkwadoma.domain.model.financier.FinancierVehicleDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.FinancierInvestmentDetailResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvestmentVehicleFinancierRestMapper {


    FinancierInvestmentDetailResponse map(FinancierVehicleDetail financierVehicleDetail);
}
