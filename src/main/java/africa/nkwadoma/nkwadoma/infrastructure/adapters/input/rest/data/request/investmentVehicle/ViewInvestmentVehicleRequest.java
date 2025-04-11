package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ViewInvestmentVehicleRequest {
    private int pageSize;
    private int pageNumber;
    private InvestmentVehicleType investmentVehicleType;
    private InvestmentVehicleStatus investmentVehicleStatus;
    private String sortField;
    private String userId;
}
