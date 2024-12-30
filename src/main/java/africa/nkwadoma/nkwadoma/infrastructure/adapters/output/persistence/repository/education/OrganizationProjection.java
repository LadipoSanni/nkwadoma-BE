package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

public interface OrganizationProjection {
    String getOrganizationId();
    String getName();
    int getLoanRequestCount();
    String getLogoImage();
}
