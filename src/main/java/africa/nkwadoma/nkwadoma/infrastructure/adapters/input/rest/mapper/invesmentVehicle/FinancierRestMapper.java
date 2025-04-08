package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.invesmentVehicle;

import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.FinancierVehicleDetail;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentSummary;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.KycRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.FinancierRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.FinancierDashboardResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.FinancierInvestmentDetailResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.KycResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.FinancierResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.NextOfKinMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.UserIdentityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserIdentityMapper.class, NextOfKinMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinancierRestMapper {
    @Mapping(target = "id", source = "financierId")
    Financier map(FinancierRequest financierRequest);
    @Mapping( source = "userIdentity", target = "userIdentity")
    @Mapping( source = "userIdentity.nextOfKin", target = "nextOfKin")
    FinancierResponse map(Financier financier);

    @Mapping(target = "financierName", expression = "java(financier.getUserIdentity().getFirstName() + ' ' + financier.getUserIdentity().getLastName())")
    @Mapping(target = "financierType", source="financierType")
    @Mapping(target = "nextOfKin", source="userIdentity.nextOfKin")
    @Mapping(target = "taxId", source="userIdentity.taxId")
    @Mapping(target = "totalAmountInvested", source="totalAmountInvested")
    @Mapping(target = "email", source="userIdentity.email")
    @Mapping(target = "phoneNumber", source="userIdentity.phoneNumber")
    @Mapping(target = "address", source="userIdentity.address")
    @Mapping(target = "rcNumber", source = "rcNumber")
    @Mapping(target = "totalNumberOfInvestment", source = "numberOfInvestment")
    FinancierDashboardResponse mapToDashboardResponse(Financier financier);

    KycResponse mapToFinancierResponse(Financier financier);

    @Mapping(target = "userIdentity.nextOfKin.firstName", source = "kycRequest.nextOfKinFirstName")
    @Mapping(target = "userIdentity.nextOfKin.lastName", source = "kycRequest.nextOfKinLastName")
    @Mapping(target = "userIdentity.nextOfKin.phoneNumber", source = "kycRequest.nextOfKinPhoneNumber")
    @Mapping(target = "userIdentity.nextOfKin.email", source = "kycRequest.nextOfKinEmail")
    @Mapping(target = "userIdentity.nextOfKin.contactAddress", source = "kycRequest.nextOfKinContactAddress")
    @Mapping(target = "userIdentity.nextOfKin.nextOfKinRelationship", source = "kycRequest.relationshipWithNextOfKin")
    @Mapping(target = "userIdentity.bankDetail.bankName", source = "kycRequest.bankName")
    @Mapping(target = "userIdentity.bankDetail.bankNumber", source = "kycRequest.bankNumber")
    @Mapping(target = "userIdentity.taxId", source = "kycRequest.taxId")
    @Mapping(target = "userIdentity.nin", source = "kycRequest.nin")
    Financier map(KycRequest kycRequest);

    @Mapping(target = "userIdentity.id", source = "userId")
    Financier map(FinancierRequest financierRequest, String userId);

    @Mapping(target = "numberOfInvestment", source = "numberOfInvestment")
    @Mapping(target = "totalAmountInvested", source = "totalAmountInvested")
    @Mapping(target = "totalIncomeEarned", source = "totalIncomeEarned")
    @Mapping(target = "portfolioValue", source = "portfolioValue")
    @Mapping(target = "investmentSummaries", source = "investmentSummaries")
    FinancierInvestmentDetailResponse mapToFinancierDetailResponse(FinancierVehicleDetail financierVehicleDetail);

}