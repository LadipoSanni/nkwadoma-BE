package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import africa.nkwadoma.nkwadoma.domain.enums.ServiceProvider;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdentityVerificationFailureRecordRequest {
    private String email;
    private String reason;
    private String referralId;
    private ServiceProvider serviceProvider;
}
