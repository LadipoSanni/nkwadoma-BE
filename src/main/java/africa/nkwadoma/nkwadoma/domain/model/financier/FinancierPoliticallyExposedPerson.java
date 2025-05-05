package africa.nkwadoma.nkwadoma.domain.model.financier;

import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Builder
public class FinancierPoliticallyExposedPerson {
    private String id;
    private Financier financier;
    private PoliticallyExposedPerson politicallyExposedPerson;

    public void validate() throws MeedlException {
        log.warn("Validating financier politically exposed person details.");
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        MeedlValidator.validateUUID(financier.getId(), FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        MeedlValidator.validateObjectInstance(this.politicallyExposedPerson, "Provide a politically exposed person. Cannot be empty.");
        MeedlValidator.validateUUID(this.politicallyExposedPerson.getId(), "Politically exposed person id is required");
    }
}
