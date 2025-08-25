package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FinancierType;

import java.math.BigDecimal;

public interface FinancierProjection {


    String getId();
    String getName();
    FinancierType getFinancierType();
    ActivationStatus getActivationStatus();
    BigDecimal getAmountInvested();
    BigDecimal getTotalAmountInvested();
    BigDecimal getAmountEarned();
    BigDecimal getPayout();
    BigDecimal getPortfolioValue();
    String getInvitedBy();
    String getEmail();
    String getIdentity();

}
