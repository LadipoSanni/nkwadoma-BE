package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.OrganizationEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TrainingInstituteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private int numberOfPrograms;
    @ManyToOne
    private OrganizationEntity organizationEntity;
}
