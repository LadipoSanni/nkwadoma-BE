package africa.nkwadoma.nkwadoma.domain.model.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleDesignation;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.FinancierEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
public class InvestmentVehicleFinancier {

    private String id;
    private BigDecimal amountInvested;
    private Financier financier;
    private InvestmentVehicle investmentVehicle;
    private Set<InvestmentVehicleDesignation> investmentVehicleDesignation;
    private LocalDateTime dateInvestment;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(financier, "Financier being added to investment vehicle can not be empty");
        MeedlValidator.validateObjectInstance(investmentVehicle, "Investment vehicle being added to financier can not be empty");
        MeedlValidator.validateUUID(financier.getId(), "Financier being added to an investment vehicle must have a valid userId");
        MeedlValidator.validateUUID(investmentVehicle.getId(), "Investment vehicle being added to a financier must have a valid id");
    }
}
