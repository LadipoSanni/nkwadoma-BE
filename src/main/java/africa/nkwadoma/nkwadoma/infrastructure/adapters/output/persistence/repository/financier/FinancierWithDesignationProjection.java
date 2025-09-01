package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier;

import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleDesignation;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierEntity;

import java.math.BigDecimal;
import java.util.Set;

public interface FinancierWithDesignationProjection {
    FinancierEntity getFinancier();
    Set<InvestmentVehicleDesignation> getInvestmentVehicleDesignation();
    String getFinancierName();
    BigDecimal getTotalAmountInvested();
    Integer getNumberOfInvestments();
}

