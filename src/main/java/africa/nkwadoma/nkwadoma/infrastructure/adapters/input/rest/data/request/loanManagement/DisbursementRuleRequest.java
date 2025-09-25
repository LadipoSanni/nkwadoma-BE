package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisbursementRuleRequest {
    private String id;
    private String name;
    private String query;
}
