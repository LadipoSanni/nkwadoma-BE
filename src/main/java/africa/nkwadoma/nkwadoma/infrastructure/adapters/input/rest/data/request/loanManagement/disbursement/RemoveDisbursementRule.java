package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.disbursement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveDisbursementRule {
    private String id;
    private String loanId;
}
