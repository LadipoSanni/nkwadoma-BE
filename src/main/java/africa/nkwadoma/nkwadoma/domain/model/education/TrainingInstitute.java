package africa.nkwadoma.nkwadoma.domain.model.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingInstitute {
    private String id;
    private int numberOfPrograms;
    private OrganizationEntity organizationEntity;
}
