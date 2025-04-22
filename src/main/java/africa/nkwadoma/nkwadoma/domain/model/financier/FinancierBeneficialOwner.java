package africa.nkwadoma.nkwadoma.domain.model.financier;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class FinancierBeneficialOwner {
    private String id;
    private Financier financier;
    private BeneficialOwner beneficialOwner;

    public void validate() {
        log.warn("Nothing is being validated.");
    }
}
