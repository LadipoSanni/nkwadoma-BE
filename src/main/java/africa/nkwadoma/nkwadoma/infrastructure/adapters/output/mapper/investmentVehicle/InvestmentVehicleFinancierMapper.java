package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.FinancierVehicleDetails;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.FinancierInvestmentDetailsResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.FinancierEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleFinancierEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses = FinancierMapper.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InvestmentVehicleFinancierMapper {

    InvestmentVehicleFinancierEntity toInvestmentVehicleFinancierEntity(InvestmentVehicleFinancier investmentVehicleFinancier);

    InvestmentVehicleFinancier toInvestmentVehicleFinancier(InvestmentVehicleFinancierEntity investmentVehicleFinancierEntity);

    List<InvestmentVehicleFinancier> toInvestmentVehicleFinancier(List<InvestmentVehicleFinancierEntity> investmentVehicleFinancierEntity);

    FinancierInvestmentDetailsResponse map(FinancierVehicleDetails financierVehicleDetails);

    /*@Mapping(source = "investmentVehicle.name", target = "investmentVehicleName")
    @Mapping(source = "investmentVehicle.investmentVehicleType", target = "investmentVehicleType")
    @Mapping(source = "dateInvested", target = "dateInvested")
    @Mapping(source = "amountInvested", target = "amountInvested")
    @Mapping(source = "investmentVehicle.netAssetValue", target = "netAssetValue")
    @Mapping(source = "investmentVehicle.startDate", target = "investmentStartDate")
    @Mapping(source = "investmentVehicle.maturityDate", target = "maturityDate")
    @Mapping(source = "investmentVehicleDesignation", target = "designation")
    @Mapping(source = "investmentVehicle.percentageOfPortfolio", target = "percentageOfPortfolio")
    @Mapping(source = "investmentVehicle.incomeEarned", target = "incomeEarned")
    @Mapping(source = "investmentVehicle.operationStatus", target = "operationStatus")
    @Mapping(source = "investmentVehicle.distributionStatus", target = "vehicleDistributionStatus")
    @Mapping(source = "investmentVehicle.closureStatus", target = "vehicleClosureStatus")
    @Mapping(source = "investmentVehicle.visibilityStatus", target = "vehicleVisibilityStatus")
//    InvestmentVehicleDetails toInvestmentVehicleDetails(InvestmentVehicleFinancier financier);

    List<InvestmentVehicleDetails> toInvestmentVehicleDetailsList(List<InvestmentVehicleFinancier> financiers);
    */
}
