package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

public interface LoanMetricsProjection {

     Integer getTotalNumberOfLoans();
     Double getLoanReferralPercentage();
     Double getLoanRequestPercentage();
     Double getLoanDisbursalPercentage();
     Double getLoanOfferPercentage();

}
