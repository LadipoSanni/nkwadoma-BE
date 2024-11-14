package africa.nkwadoma.nkwadoma.domain.model.identity;

import africa.nkwadoma.nkwadoma.domain.enums.ServiceProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class IdentityVerificationFailureRecord {
    private String id;
    private String email;
    private String reason;
    private String referralId;
    private ServiceProvider serviceProvider;
}
