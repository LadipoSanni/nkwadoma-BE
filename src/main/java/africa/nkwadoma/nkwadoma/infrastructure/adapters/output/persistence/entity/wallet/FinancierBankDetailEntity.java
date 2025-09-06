package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.wallet;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancierBankDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne
    private FinancierEntity financierEntity;
    @ManyToOne
    private BankDetailEntity bankDetailEntity;
}
