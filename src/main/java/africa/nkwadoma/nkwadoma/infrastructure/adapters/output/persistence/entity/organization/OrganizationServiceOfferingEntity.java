package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OrganizationServiceOfferingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne
    private ServiceOfferingEntity serviceOfferingEntity;
    private String organizationId;
}
