package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.meedlPortfolio;


import africa.nkwadoma.nkwadoma.domain.enums.meedlportfolio.PortfolioRiskLevel;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class PortfolioResponse {

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
    private double loanOfferPercentage;
    private double uploadLoanPercentage;
    private int numberOfLoanees;
    private int numberOfOrganizations;
    private int numberOfLoanProducts;
    private BigDecimal historicalDebt ;
    private BigDecimal disbursedLoanAmount;
    private BigDecimal netLoanPortfolio;
    private BigDecimal totalAmountEarned;
    private String assetUnderManagement;
    private double aumPercentage;
    private BigDecimal totalAvailable;
    private BigDecimal fundManagerFee;
    private BigDecimal trusteeFee;
    private double percentageGain;
    private double percentageLoss;
    private PortfolioRiskLevel portfolioRiskLevel;
    private List<InvestmentVehicle> topPerformingInvestmentVehicles;
    private List<InvestmentVehicle> underPerformingInvestmentVehicles;
}
