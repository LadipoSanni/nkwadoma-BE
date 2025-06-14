package africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio;


import africa.nkwadoma.nkwadoma.domain.enums.meedlPortfolio.PortfolioRiskLevel;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@Builder
public class Portfolio {

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
    private double loanOfferPercentage;
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

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectName(portfolioName,"portfolio cannot be empty",
                "portfolio");
    }

}
