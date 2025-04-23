package africa.nkwadoma.nkwadoma.domain.model.financier;

import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class FinancierBeneficialOwner {
    private String id;
    private Financier financier;
    private BeneficialOwner beneficialOwner;

    public void validate() throws MeedlException {
        log.warn("Validating financier beneficial owner details.");
        MeedlValidator.validateObjectInstance(financier, FinancierMessages.EMPTY_FINANCIER_PROVIDED.getMessage());
        MeedlValidator.validateUUID(financier.getId(), FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        MeedlValidator.validateObjectInstance(beneficialOwner, "Provide a beneficial owner. Cannot be empty.");
        MeedlValidator.validateUUID(beneficialOwner.getId(), "Beneficial owner id is required");
    }
}
