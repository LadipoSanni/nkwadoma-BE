package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentSummary;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentVehicle.InvestmentVehicleProjection;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = "spring"  , uses = {InvestmentVehicleFinancierMapper.class, VehicleOperationMapper.class} ,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InvestmentVehicleMapper {

    @Mapping(target = "totalAvailableAmount", source = "totalAvailableAmount")
    @Mapping(target = "operation", source = "vehicleOperation")
    @Mapping(target = "closure", source = "vehicleClosureStatus")
    InvestmentVehicleEntity toInvestmentVehicleEntity(InvestmentVehicle investmentVehicle);

    @InheritInverseConfiguration
    InvestmentVehicle toInvestmentVehicle(InvestmentVehicleEntity investmentEntity);

    void updateInvestmentVehicle(@MappingTarget InvestmentVehicle foundInvestmentVehicle, InvestmentVehicle investmentVehicle);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "investmentVehicleType", source = "investmentVehicleType")
    @Mapping(target = "dateInvested", source = "dateInvested")
    @Mapping(target = "amountInvested", source = "amountInvested")
    @Mapping(target = "netAssetValue", source = "netAssetValue")
    @Mapping(target = "percentageOfPortfolio", source = "percentageOfPortfolio")
    @Mapping(target = "investmentStartDate", source = "startDate")
    @Mapping(target = "maturityDate", source = "maturityDate")
    @Mapping(target = "incomeEarned", source = "incomeEarned")
    @Mapping(target = "designations", source = "designations")
    @Mapping(target = "operationStatus", source = "vehicleOperation.operationStatus")
    @Mapping(target = "couponDistributionStatus", source = "vehicleOperation.couponDistributionStatus")
    @Mapping(target = "vehicleClosureStatus", source = "vehicleClosureStatus")
    @Mapping(target = "investmentVehicleVisibility", source = "investmentVehicleVisibility")
    List<InvestmentSummary> toInvestmentSummaries(List<InvestmentVehicle> investmentVehicleForFinancier);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "investmentVehicleType", source = "investmentVehicleType")
    @Mapping(target = "dateInvested", source = "dateInvested")
    @Mapping(target = "netAssetValue", source = "netAssetValue", defaultValue = "0")
    @Mapping(target = "netAssetValueInPercent", source = "netAssetValueInPercent")
    @Mapping(target = "percentageOfPortfolio", source = "percentageOfPortfolio")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "maturityDate", source = "maturityDate")
    @Mapping(target = "incomeEarned", source = "incomeEarned", defaultValue = "0")
    @Mapping(target = "designations", source = "designations")
    @Mapping(target = "operationStatus", source = "vehicleOperation.operationStatus")
    @Mapping(target = "couponDistributionStatus", source = "vehicleOperation.couponDistributionStatus")
    @Mapping(target = "vehicleClosureStatus", source = "vehicleClosureStatus")
    @Mapping(target = "investmentVehicleVisibility", source = "investmentVehicleVisibility")
    @Mapping(target = "fundManager", source="fundManager")
    @Mapping(target = "incomeInterval", source = "incomeInterval")
    @Mapping(target = "talentFunded", source = "talentFunded", defaultValue = "0")
    @Mapping(target = "interestRateOffered", source = "interestRateOffered")
    @Mapping(target = "deployingStatus", source = "vehicleOperation.deployingStatus")
    @Mapping(target = "mandate", source = "mandate")
    @Mapping(target = "fundRaisingStatus", source = "vehicleOperation.fundRaisingStatus")
    @Mapping(target = "minimumInvestmentAmount", source = "minimumInvestmentAmount")
    @Mapping(target = "amountInvested", source = "amountFinancierInvested", defaultValue = "0")
    InvestmentSummary toInvestmentSummary(InvestmentVehicle investmentVehicle);

    @Mapping(target = "vehicleOperation.couponDistributionStatus", source = "couponDistributionStatus")
    @Mapping(target = "vehicleOperation.deployingStatus", source = "deployingStatus")
    @Mapping(target = "vehicleOperation.fundRaisingStatus", source = "fundRaising")
    @Mapping(target = "vehicleClosureStatus.recollectionStatus", source = "recollectionStatus")
    @Mapping(target = "vehicleClosureStatus.maturity", source = "maturity")
    InvestmentVehicle mapInvestmentvehicleProjecttionToInvestmentVehicle(InvestmentVehicleProjection investmentVehicleProjection);
}
