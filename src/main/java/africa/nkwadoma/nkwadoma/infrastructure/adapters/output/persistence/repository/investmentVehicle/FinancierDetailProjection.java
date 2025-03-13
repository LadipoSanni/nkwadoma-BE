package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.OrganizationProjection;

public interface FinancierDetailProjection {
    String getId();
    String getFinancierType();

    UserProjection getIndividual();
    OrganizationProjection getOrganizationEntity();

    interface OrganizationProjection{

    }

    interface UserProjection {
        String getEmail();
        String getPhoneNumber();
        String getResidentialAddress();

        // Fetch Next of Kin Details
        NextOfKinProjection getNextOfKin();
    }

    interface NextOfKinProjection {
        String getFirstName();
        String getLastName();
        String getPhoneNumber();
        String getContactAddress();
        String getEmail();
        String getNextOfKinRelationship();
    }
}
