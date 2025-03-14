package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle;

public interface FinancierDetailProjection {
    String getId();
    String getFinancierType();

    UserProjection getIndividual();
    OrganizationProjection getOrganizationEntity();

    interface OrganizationProjection{

    }

    interface UserProjection {
        String getId();
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
