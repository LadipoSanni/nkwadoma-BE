package africa.nkwadoma.nkwadoma.domain.model.loan;

import lombok.*;

@Setter
@Getter
@Builder
@RequiredArgsConstructor
public class DisbursementRule {
    private String id;
    private String query;

    public void validate() {

    }
}
