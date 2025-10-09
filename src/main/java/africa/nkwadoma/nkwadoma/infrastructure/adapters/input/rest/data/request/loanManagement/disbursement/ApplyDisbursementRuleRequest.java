package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.disbursement;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApplyDisbursementRuleRequest {
    private String id;
    private List<String> loanIds;

}
