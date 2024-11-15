package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity;

import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;
import jakarta.persistence.*;
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
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @OneToOne
    private UserEntity loanee;
    @OneToOne
    private LoaneeLoanDetailEntity loaneeLoanDetail;
    @Enumerated(EnumType.STRING)
    private LoaneeStatus loaneeStatus;
    private LocalDateTime referralDateTime;
    private String referredBy;
}
