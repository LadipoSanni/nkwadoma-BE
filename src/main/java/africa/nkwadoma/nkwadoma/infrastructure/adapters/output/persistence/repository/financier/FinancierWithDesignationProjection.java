package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier;

import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleDesignation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface FinancierWithDesignationProjection {
    String getFinancier();
    List<String> getInvestmentVehicleDesignation(); // <-- changed from Set<InvestmentVehicleDesignation>
    String getFinancierName();
    BigDecimal getTotalAmountInvested();
    Integer getNumberOfInvestments();
}


