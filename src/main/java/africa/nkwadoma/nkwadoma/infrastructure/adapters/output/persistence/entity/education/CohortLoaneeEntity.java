package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education;


import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoaneeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Setter
@Getter
@Entity
public class CohortLoaneeEntity {

    @Id
    @UuidGenerator
    private String id;
    @OneToOne
    private LoaneeEntity loanee;
    private String cohort;
}
