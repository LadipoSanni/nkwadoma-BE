package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity.*;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface NextOfKinRepository extends JpaRepository<NextOfKinEntity, String> {
    Optional<NextOfKinEntity> findByEmail(String email);
    Optional<NextOfKinEntity> findByLoaneeEntityId(String loaneeId);
//    NextOfKinProjection findNextOfKin(String loaneeId);
}
