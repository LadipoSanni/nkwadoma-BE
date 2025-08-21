package africa.nkwadoma.nkwadoma.domain.model.education;

import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class InstituteMetrics {

    private String id;
    private OrganizationIdentity organization;
    private int numberOfPrograms;
    private int numberOfLoanees;
    private int stillInTraining;
    private int numberOfCohort;}
