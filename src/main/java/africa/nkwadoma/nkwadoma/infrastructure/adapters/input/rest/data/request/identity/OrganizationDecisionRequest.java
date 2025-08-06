package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrganizationDecisionRequest {

    private String organizationId;
    private ActivationStatus activationStatus;
}
