package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

public interface OrganizationProjection {
    String getOrganizationId();
    String getName();
    int getLoanRequestCount();
    int getLoanDisbursalCount();
    int getLoanReferralCount();
    int getLoanOfferCount();
    String getLogoImage();
    int getNumberOfLoanees();
    int getNumberOfCohort();
    int getNumberOfPrograms();
}
