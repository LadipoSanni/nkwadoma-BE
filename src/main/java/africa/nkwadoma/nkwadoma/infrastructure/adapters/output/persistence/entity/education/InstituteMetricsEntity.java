package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Setter
@Getter
@Entity
public class InstituteMetricsEntity {

    @Id
    @UuidGenerator
    private String id;
    @OneToOne
    private OrganizationEntity organization;
    private int numberOfPrograms;
    private int numberOfLoanees;
    private int stillInTraining;
    private int numberOfCohort;

}


