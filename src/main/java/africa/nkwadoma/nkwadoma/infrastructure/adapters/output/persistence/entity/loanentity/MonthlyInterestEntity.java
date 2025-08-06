package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class MonthlyInterestEntity {

    @Id
    @UuidGenerator
    private String id;
    private LocalDateTime createdAt;
    private BigDecimal interest;
    @ManyToOne
    private LoaneeLoanDetailEntity loaneeLoanDetail;
}
