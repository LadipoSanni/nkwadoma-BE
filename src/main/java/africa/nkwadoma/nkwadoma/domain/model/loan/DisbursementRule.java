package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import lombok.*;

@Setter
@Getter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class DisbursementRule {
    private String id;
    private String name;
    private String query;
    private ActivationStatus activationStatus;

    public void validate() {

    }
}
