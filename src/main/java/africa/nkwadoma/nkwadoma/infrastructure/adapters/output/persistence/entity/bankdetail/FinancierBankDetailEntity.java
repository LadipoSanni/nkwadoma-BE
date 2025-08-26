package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.bankdetail;

import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
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
