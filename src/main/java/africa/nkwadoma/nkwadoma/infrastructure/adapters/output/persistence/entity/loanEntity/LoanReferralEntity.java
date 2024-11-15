package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity;


import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Setter
@Getter
@Entity
public class LoanReferralEntity {

    @Id
    @UuidGenerator
    private String id;
    @ManyToOne
    private LoaneeEntity loanee;
    @Enumerated(EnumType.STRING)
    private LoanReferralStatus loanReferralStatus;


}
