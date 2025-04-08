package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentSummary;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleEntity;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = "spring"  , uses = {InvestmentVehicleFinancierMapper.class, VehicleOperationMapper.class} ,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InvestmentVehicleMapper {

    @Mapping(target = "totalAvailableAmount", source = "totalAvailableAmount")
    @Mapping(target = "operation", source = "vehicleOperation")
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
    @Mapping(target = "amountInvested", source = "amountInvested")
    @Mapping(target = "netAssetValue", source = "netAssetValue")
    @Mapping(target = "percentageOfPortfolio", source = "percentageOfPortfolio")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "maturityDate", source = "maturityDate")
    @Mapping(target = "incomeEarned", source = "incomeEarned")
    @Mapping(target = "designations", source = "designations")
    @Mapping(target = "operationStatus", source = "vehicleOperation.operationStatus")
    @Mapping(target = "couponDistributionStatus", source = "vehicleOperation.couponDistributionStatus")
    @Mapping(target = "vehicleClosureStatus", source = "vehicleClosureStatus")
    @Mapping(target = "investmentVehicleVisibility", source = "investmentVehicleVisibility")
    InvestmentSummary toInvestmentSummary(InvestmentVehicle investmentVehicle);
}
