package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlPortfolio;


import jakarta.persistence.Column;
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
    @Column(nullable = false)
    private int totalNumberOfInvestmentVehicle = 0;
    @Column(nullable = false)
    private int totalNumberOfCommercialFundsInvestmentVehicle = 0;
    @Column(nullable = false)
    private int totalNumberOfEndowmentFundsInvestmentVehicle = 0;
    @Column(nullable = false)
    private int totalNumberOfFinancier = 0;
    @Column(nullable = false)
    private int totalNumberOfIndividualFinancier = 0;
    @Column(nullable = false)
    private int totalNumberOfInstitutionalFinancier = 0;
    @Column(nullable = false)
    private int totalNumberOfLoans = 0;
    @Column(nullable = false)
    private double loanReferralPercentage = 0.0;
    @Column(nullable = false)
    private double loanRequestPercentage = 0.0;
    @Column(nullable = false)
    private double loanDisbursalPercentage = 0.0;

}
