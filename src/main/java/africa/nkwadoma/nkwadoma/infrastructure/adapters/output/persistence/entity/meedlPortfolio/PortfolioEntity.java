package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlPortfolio;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Setter
@Getter
@Entity
public class PortfolioEntity {

    @Id
    @UuidGenerator
    private String id;
    private String portfolioName;
    private int totalNumberOfInvestmentVehicle;
    private int totalNumberOfCommercialFundsInvestmentVehicle;
    private int totalNumberOfEndowmentFundsInvestmentVehicle;
    private int totalNumberOfFinancier;
    private int totalNumberOfIndividualFinancier;
    private int totalNumberOfInstitutionalFinancier;
    private int totalNumberOfLoans;
    private double loanReferralPercentage;
    private double loanRequestPercentage;
    private double loanDisbursalPercentage;

}
