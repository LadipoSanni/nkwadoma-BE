package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity;

public interface NextOfKinProjection {
    String getFirstName();
    String getLastName();
    String getEmail();
    String getPhoneNumber();
    String getNextOfKinRelationship();
}
