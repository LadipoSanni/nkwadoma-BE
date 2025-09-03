package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.wallet;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String bankName;
    private String bankNumber;
    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus;
}
