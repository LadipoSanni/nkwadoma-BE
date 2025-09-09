package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier;

import africa.nkwadoma.nkwadoma.domain.enums.AccreditationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FinancierType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface FinancierProjection {


    String getId();
    String getName();
    LocalDateTime getCreatedAt();
    FinancierType getFinancierType();
    ActivationStatus getActivationStatus();
    AccreditationStatus getAccreditationStatus();
    BigDecimal getAmountInvested();
    BigDecimal getTotalAmountInvested();
    BigDecimal getAmountEarned();
    BigDecimal getPayout();
    BigDecimal getPortfolioValue();
    String getInvitedBy();
    String getEmail();
    String getIdentity();
    String getCooperateAdminEmail();
    String getCooperateAdminName();
    String getPhoneNumber();
    String getAddress();


}
