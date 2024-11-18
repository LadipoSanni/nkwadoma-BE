package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity;

import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class LoanEntity {
    @Id
    @UuidGenerator
    private String id;
    @OneToOne
    private LoaneeEntity loanee;
    private String loanAccountId;
    private LocalDateTime startDate;
    private LocalDateTime lastUpdatedDate;
}
