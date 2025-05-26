package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import jakarta.persistence.*;
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
    private LoaneeEntity loaneeEntity;
    private String loanAccountId;
    private String loanOfferId;
    private LocalDateTime startDate;
    private LocalDateTime lastUpdatedDate;
    @Enumerated(EnumType.STRING)
    private LoanStatus loanStatus;
}
