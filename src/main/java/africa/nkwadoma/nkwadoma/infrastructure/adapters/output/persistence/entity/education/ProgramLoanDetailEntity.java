package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education;

import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.education.ProgramLoanDetail;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;


@Getter
@Setter
@Entity
public class ProgramLoanDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @OneToOne
    private ProgramEntity program;
    private BigDecimal amountRequested = BigDecimal.ZERO;
    private BigDecimal outstandingAmount = BigDecimal.ZERO;
    private BigDecimal amountReceived = BigDecimal.ZERO;
    private BigDecimal amountRepaid = BigDecimal.ZERO;
    private BigDecimal interestIncurred = BigDecimal.ZERO;

}
