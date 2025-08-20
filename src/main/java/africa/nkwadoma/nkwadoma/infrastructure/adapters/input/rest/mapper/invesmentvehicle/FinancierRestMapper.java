package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.invesmentvehicle;

import africa.nkwadoma.nkwadoma.domain.model.financier.CooperateFinancier;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.financier.FinancierVehicleDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.InviteColleagueRequest;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.Cooperation;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.CooperationRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.KycRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.FinancierRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.NextOfKinMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.UserIdentityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {UserIdentityMapper.class, NextOfKinMapper.class, InvestmentVehicleRestMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinancierRestMapper {
    @Mapping(target = "id", source = "financierId")
    Financier map(FinancierRequest financierRequest);
    @Mapping( source = "userIdentity", target = "userIdentity")
    @Mapping( source = "userIdentity.nextOfKin", target = "nextOfKin")
    @Mapping( source = "cooperation.name", target = "organizationName")
    @Mapping( source = "investmentVehicleDesignation", target = "investmentVehicleRole")
    FinancierResponse map(Financier financier);

    @Mapping(target = "firstName", source="userIdentity.firstName")
    @Mapping(target = "lastName", source="userIdentity.lastName")
    @Mapping(target = "organizationName", source = "cooperation.name")
    @Mapping(target = "financierType", source="financierType")
    @Mapping(target = "nextOfKin", source="userIdentity.nextOfKin")
    @Mapping(target = "taxId", source="userIdentity.taxId")
    @Mapping(target = "totalAmountInvested", source="totalAmountInvested")
    @Mapping(target = "email", source="userIdentity.email")
    @Mapping(target = "phoneNumber", source="userIdentity.phoneNumber")
    @Mapping(target = "address", source="userIdentity.address")
    @Mapping(target = "rcNumber", source = "rcNumber")
    @Mapping(target = "totalNumberOfInvestment", source = "totalNumberOfInvestment")
    @Mapping(target = "investmentVehicleResponses", source = "investmentVehicles")
    FinancierDashboardResponse mapToDashboardResponse(Financier financier);

    @Mapping( target = "firstName", source = "userIdentity.firstName")
    @Mapping( target = "lastName", source = "userIdentity.lastName")
    @Mapping( target = "financierEmail", source = "userIdentity.email")
    @Mapping( target = "phoneNumber", source = "userIdentity.phoneNumber")
    @Mapping( target = "nin", source = "userIdentity.nin")
    @Mapping( target = "bvn", source = "userIdentity.bvn")
    @Mapping( target = "taxId", source = "userIdentity.taxId")
    KycResponse mapToFinancierResponse(Financier financier);

    @Mapping(target = "userIdentity.taxId", source = "kycRequest.taxId")
    @Mapping(target = "userIdentity.nin", source = "kycRequest.nin")
    @Mapping(target = "userIdentity.bvn", source = "kycRequest.bvn")
    @Mapping(target = "userIdentity.phoneNumber", source = "kycRequest.phoneNumber")
    @Mapping(target = "rcNumber", source = "rcNumber")
    Financier map(KycRequest kycRequest);

    @Mapping(target = "userIdentity.id", source = "userId")
    Financier map(FinancierRequest financierRequest, String userId);

    @Mapping(target = "numberOfInvestment", source = "numberOfInvestment")
    @Mapping(target = "totalAmountInvested", source = "totalAmountInvested")
    @Mapping(target = "totalIncomeEarned", source = "totalIncomeEarned")
    @Mapping(target = "portfolioValue", source = "portfolioValue")
    @Mapping(target = "investmentSummaries", source = "investmentSummaries")
    FinancierInvestmentDetailResponse mapToFinancierDetailResponse(FinancierVehicleDetail financierVehicleDetail);

    @Mapping(target = "portfolioValue", source = "portfolioValue")
    @Mapping(target = "amountInvested", source = "amountInvested")
    @Mapping(target = "dateInvested", source = "dateInvested")
    @Mapping(target = "incomeEarned", source = "incomeEarned")
    @Mapping(target = "netAssertValue", source = "netAssertValue")
    @Mapping(target = "investmentId", source = "investmentId")
    @Mapping(target = "investmentVehicleType", source = "investmentVehicleType")
    FinancierInvestmentResponse mapToFinancierInvestment(Financier financier);

    @Mapping(target = "userIdentity.id", source = "userId")
    @Mapping(target = "privacyPolicyAccepted", source = "privacyPolicyDecision")
    Financier map(String userId, boolean privacyPolicyDecision);

    @Mapping(target = "userIdentity.firstName", source = "firstName")
    @Mapping(target = "userIdentity.lastName", source = "lastName")
    @Mapping(target = "userIdentity.email", source = "email")
    @Mapping(target = "userIdentity.role", source = "role")
    Financier mapInviteColleagueRequestToFinancier(InviteColleagueRequest inviteColleagueRequest);

    CooperationResponse mapToCooperationResponse(Cooperation cooperation);

    Cooperation mapCooperationRequestToCooperation(CooperationRequest cooperationRequest);


    @Mapping(target = "cooperation.name", source = "organizationName")
    @Mapping(target = "cooperation.email", source = "organizationEmail")
    Financier mapToCooperateFinancier(FinancierRequest financierRequest);

    CooperateFinancierResponse mapToCooperateFinancierResponse(CooperateFinancier cooperateFinancier);
}