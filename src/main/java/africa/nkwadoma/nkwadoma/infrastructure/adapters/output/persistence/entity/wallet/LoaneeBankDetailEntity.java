package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.wallet;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoaneeBankDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne
    private LoaneeEntity loaneeEntity;
    @ManyToOne
    private BankDetailEntity bankDetailEntity;
}
