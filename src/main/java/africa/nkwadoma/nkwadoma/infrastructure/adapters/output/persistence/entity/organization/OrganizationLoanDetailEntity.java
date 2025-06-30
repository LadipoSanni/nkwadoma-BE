package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
public class OrganizationLoanDetailEntity {

    @Id
    @UuidGenerator
    private String id;
    @OneToOne
    private OrganizationEntity organization;
    private BigDecimal totalAmountRequested = BigDecimal.ZERO;
    private BigDecimal totalOutstandingAmount = BigDecimal.ZERO;
    private BigDecimal totalAmountReceived = BigDecimal.ZERO;
    private BigDecimal totalAmountRepaid = BigDecimal.ZERO;

}
