package africa.nkwadoma.nkwadoma.domain.model.identity;

import africa.nkwadoma.nkwadoma.domain.enums.ServiceProvider;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class IdentityVerificationFailureRecord {
    private String id;
    private String email;
    private String reason;
    private String userId;
    private ServiceProvider serviceProvider;

}
