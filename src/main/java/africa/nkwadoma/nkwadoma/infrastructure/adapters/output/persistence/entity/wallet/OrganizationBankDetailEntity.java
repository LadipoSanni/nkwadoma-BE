package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.wallet;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationBankDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne
    private OrganizationEntity organizationEntity;
    @ManyToOne
    private BankDetailEntity bankDetailEntity;
}
