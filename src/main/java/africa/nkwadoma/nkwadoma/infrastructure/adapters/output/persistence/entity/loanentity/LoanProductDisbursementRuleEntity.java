package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity;

import africa.nkwadoma.nkwadoma.domain.model.loan.DisbursementRule;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity
public class LoanProductDisbursementRuleEntity {
    @Id
    @UuidGenerator
    private String id;
    @ManyToOne
    private LoanProductEntity loanProductEntity;
    @ManyToOne
    private DisbursementRuleEntity disbursementRuleEntity;
}
