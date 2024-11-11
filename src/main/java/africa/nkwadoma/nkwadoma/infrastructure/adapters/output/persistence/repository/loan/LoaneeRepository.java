package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoaneeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoaneeRepository extends JpaRepository<LoaneeEntity,String> {
    LoaneeEntity findByLoaneeEmail(String email);

    List<LoaneeEntity> findAllByLoaneeEmail(String email);
}
