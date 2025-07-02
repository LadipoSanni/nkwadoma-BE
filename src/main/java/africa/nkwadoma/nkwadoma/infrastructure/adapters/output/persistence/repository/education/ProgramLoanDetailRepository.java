package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramLoanDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramLoanDetailRepository extends JpaRepository<ProgramLoanDetailEntity, String> {
    ProgramLoanDetailEntity findByProgramId(String id);
}
