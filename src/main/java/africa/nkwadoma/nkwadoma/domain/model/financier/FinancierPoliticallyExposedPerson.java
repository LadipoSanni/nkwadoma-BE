package africa.nkwadoma.nkwadoma.domain.model.financier;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FinancierPoliticallyExposedPerson {
    private String id;
    private Financier financier;
    private PoliticallyExposedPerson politicallyExposedPerson;
}
