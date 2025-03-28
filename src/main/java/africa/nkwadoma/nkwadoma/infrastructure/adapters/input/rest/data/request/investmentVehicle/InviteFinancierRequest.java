package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InviteFinancierRequest {
    private List<FinancierRequest> financierRequests;
    private List<String> financierIds;
    private String investmentVehicleId;
}
