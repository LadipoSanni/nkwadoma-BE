package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlPortfolio;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.beans.factory.annotation.Value;

@Setter
@Getter
@Entity
public class PortfolioEntity {

    @Id
    @UuidGenerator
    private String id;
    private String portfolioName;
    private int totalNumberOfInvestmentVehicle = 0;
    private int totalNumberOfCommercialFundsInvestmentVehicle = 0;
    private int totalNumberOfEndowmentFundsInvestmentVehicle = 0;
    private int totalNumberOfFinancier = 0;
    private int totalNumberOfIndividualFinancier = 0;
    private int totalNumberOfInstitutionalFinancier = 0;
    private int totalNumberOfLoans = 0;
    private double loanReferralPercentage = 0.0;
    private double loanRequestPercentage = 0.0;
    private double loanDisbursalPercentage = 0.0;

}
