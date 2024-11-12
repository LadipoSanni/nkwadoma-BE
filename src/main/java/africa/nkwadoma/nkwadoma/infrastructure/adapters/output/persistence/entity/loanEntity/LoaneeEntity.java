package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity;


import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.UserEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
public class LoaneeEntity {
    @Id
    @UuidGenerator
    private String id;
    private String cohortId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    @OneToOne(cascade = CascadeType.PERSIST)
    private UserEntity loanee;
    @OneToOne(cascade = CascadeType.PERSIST)
    private LoaneeLoanDetailEntity loaneeLoanDetail;
}
