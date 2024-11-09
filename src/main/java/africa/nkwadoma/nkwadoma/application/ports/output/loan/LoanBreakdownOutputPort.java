package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanBreakdownEntity;

import java.util.List;

public interface LoanBreakdownOutputPort {
    List<LoanBreakdown> findAllByCohortId(String id);


}
