package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.financier;

import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.Cooperation;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.NextOfKinMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.UserIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier.FinancierProjection;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.financier.FinancierWithDesignationProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses ={UserIdentityMapper.class, NextOfKinMapper.class }, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinancierMapper {
    FinancierEntity map(Financier financier);

    Financier map(FinancierEntity financierEntity);


    @Mapping(target = "amountInvested", source = "amountInvested")
    @Mapping(target = "investmentVehicleType", source = "investmentVehicle.investmentVehicleType")
    @Mapping(target = "investmentVehicleName", source = "investmentVehicle.name")
    @Mapping(target = "dateInvested", source = "dateInvested")
    @Mapping(target = "netAssertValue", source = "financier.netAssertValue")
    @Mapping(target = "portfolioValue", source = "financier.portfolioValue")
    @Mapping(target = "investmentId", source = "id")
    Financier mapToFinancierInvestment(InvestmentVehicleFinancier investmentVehicleFinancier);



    void updateCooperation(@MappingTarget  Cooperation cooperate, Cooperation cooperation);


    Financier mapProjectionToFinancier(FinancierProjection financierProjection);


    @Mapping(target = "name", source = "financierName")
    @Mapping(target = "totalAmountInvested", source = "totalAmountInvested")
    @Mapping(target = "totalNumberOfInvestment", source = "numberOfInvestments")
    @Mapping(target = "investmentVehicleDesignation", source = "investmentVehicleDesignation")
    void updateFinancierFromProjection(FinancierWithDesignationProjection projection,
                                       @MappingTarget Financier financier);
}
