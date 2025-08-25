package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.bankdetail;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntityBankDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String entityId;
    @OneToOne
    private BankDetailEntity bankDetail;
}
