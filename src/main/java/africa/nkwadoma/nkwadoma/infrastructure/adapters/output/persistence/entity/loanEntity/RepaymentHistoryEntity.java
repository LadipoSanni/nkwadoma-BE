package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity;

import africa.nkwadoma.nkwadoma.domain.enums.loanee.ModeOfPayment;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class RepaymentHistoryEntity {
    @Id
    @UuidGenerator
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String paymentDate;
    private String cohortId;
    private BigDecimal amountPaid;
    @Enumerated(EnumType.STRING)
    private ModeOfPayment modeOfPayment;
}
