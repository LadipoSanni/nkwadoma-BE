package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.disbursement.DisbursementRuleEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity
@Table(name = "loan_product_disb_rule")
public class LoanProductDisbursementRuleEntity {
    @Id
    @UuidGenerator
    private String id;
    @ManyToOne
    private LoanProductEntity loanProductEntity;
    @ManyToOne
    private DisbursementRuleEntity disbursementRuleEntity;
}
