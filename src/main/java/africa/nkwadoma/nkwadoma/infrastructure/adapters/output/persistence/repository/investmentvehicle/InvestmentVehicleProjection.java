package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentvehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public interface InvestmentVehicleProjection {

    String getId();
    String getName();
    InvestmentVehicleType getInvestmentVehicleType();
    String getMandate();
    Integer getTenure();
    BigDecimal getSize();
    BigDecimal getTotalAvailableAmount();
    Float getInterestRateOffered();
    String getTrustee();
    String getCustodian();
    String getBankPartner();
    String getFundManager();
    BigDecimal getMinimumInvestmentAmount();
    LocalDate getStartDate();
    LocalDateTime getCreatedDate();
    InvestmentVehicleStatus getInvestmentVehicleStatus();
    String getInvestmentVehicleLink();
    CouponDistributionStatus getCouponDistributionStatus();
    InvestmentVehicleMode getDeployingStatus();
    InvestmentVehicleMode getFundRaising();
    InvestmentVehicleMode getRecollectionStatus();
    String getMaturity();
    BigDecimal getAmountFinancierInvested();
    Set<InvestmentVehicleDesignation> getInvestmentVehicleDesignation();
    InvestmentVehicleVisibility getInvestmentVehicleVisibility();
    LocalDate getDateInvested();
    BigDecimal getAmountRaised();
    BigDecimal getAmountDisbursed();
    BigDecimal getAmountAvailable();
    Integer getTalentFunded();
}
