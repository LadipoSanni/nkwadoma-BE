package africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio;


import africa.nkwadoma.nkwadoma.domain.enums.meedlPortfolio.PortfolioRiskLevel;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@Builder
public class MeedlPortfolio {

    private String id;
    private String portfolioName;
    private int totalNumberOfInvestmentVehicles;
    private int totalNumberOfCommercialFundsInvestmentVehicles;
    private int totalNumberOfEndowmentFundsInvestmentVehicles;
    private int totalNumberOfFinanciers;
    private int totalNumberOfIndividualFinanciers;
    private int totalNumberOfInstitutionalFinanciers;
    private String assertUnderManagement;
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
