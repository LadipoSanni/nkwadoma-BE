package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisbursementRuleRequest {
    private String id;
    private String name;
    private String query;
    private ActivationStatus activationStatus;
}
