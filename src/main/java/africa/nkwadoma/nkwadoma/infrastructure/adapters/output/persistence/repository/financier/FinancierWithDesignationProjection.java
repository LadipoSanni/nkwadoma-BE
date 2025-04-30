package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleDesignation;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

public interface FinancierWithDesignationProjection {
    FinancierEntity getFinancier();
    Set<InvestmentVehicleDesignation> getInvestmentVehicleDesignation();
}

