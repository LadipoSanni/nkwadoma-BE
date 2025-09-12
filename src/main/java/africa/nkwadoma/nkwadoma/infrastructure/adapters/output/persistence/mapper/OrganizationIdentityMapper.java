package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;


import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.*;
import org.mapstruct.*;

import java.util.*;

@Mapper(componentModel = "spring", uses = ServiceOfferingMapper.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrganizationIdentityMapper {
    @Mapping(source = "rcNumber", target = "rcNumber")
    @Mapping(source = "tin", target = "taxIdentity")
    OrganizationEntity toOrganizationEntity(OrganizationIdentity organizationIdentity);

    @InheritInverseConfiguration
    OrganizationIdentity toOrganizationIdentity(OrganizationEntity organizationEntity);

    ServiceOffering toServiceOffering(ServiceOfferingEntity serviceOfferingEntity);
    List<ServiceOffering> toServiceOfferings(List<OrganizationServiceOfferingEntity> organizationServiceOfferings);


    List<OrganizationServiceOffering> toOrganizationServiceOfferings(List<OrganizationServiceOfferingEntity> organizationServiceOfferings);

    OrganizationIdentity updateOrganizationIdentity(@MappingTarget OrganizationIdentity organizationIdentityMapTo, OrganizationIdentity organizationIdentityMapFrom);


    @Mapping(target = "id", source = "organizationId")
    @Mapping(target = "loanRequestCount", source = "loanRequestCount")
    @Mapping(target = "loanDisbursalCount", source = "loanDisbursalCount")
    @Mapping(target = "loanOfferCount", source = "loanOfferCount")
    @Mapping(target = "loanReferralCount", source = "loanReferralCount")
    @Mapping(target = "numberOfLoanees", source = "numberOfLoanees")
    @Mapping(target = "numberOfCohort", source = "numberOfCohort")
    @Mapping(target = "numberOfPrograms", source = "numberOfPrograms")
    OrganizationIdentity projectionToOrganizationIdentity(OrganizationProjection organization);

    @Mapping(target = "totalAmountRequested", source = "amountRequested")
    @Mapping(target = "totalCurrentDebt", source = "outstandingAmount")
    @Mapping(target = "totalAmountReceived", source = "amountReceived")
    @Mapping(target = "totalDebtRepaid", source = "amountRepaid")
    @Mapping(target = "id", ignore = true)
    void mapOrganizationLoanDetailsToOrganization(@MappingTarget OrganizationIdentity organizationIdentity, OrganizationLoanDetail organizationLoanDetail);

    @Mapping(target = "id", source = "organizationId")
    @Mapping(target = "activationStatus", source = "activationStatus")
    @Mapping(target = "numberOfLoanees", source = "numberOfLoanees")
    @Mapping(target = "numberOfCohort", source = "numberOfCohort")
    @Mapping(target = "numberOfPrograms", source = "numberOfPrograms")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "websiteAddress", source = "websiteAddress")
    @Mapping(target = "debtPercentage", source = "debtPercentage")
    @Mapping(target = "repaymentRate", source = "repaymentRate")
    @Mapping(target = "requestedBy", source = "inviterFullName")
    OrganizationIdentity mapProjecttionToOrganizationIdentity(OrganizationProjection organizationProjection);

    @Mapping(target = "taxIdentity", source = "financier.userIdentity.taxId")
    @Mapping(target = "phoneNumber", source = "financier.userIdentity.phoneNumber")
    @Mapping(target = "tin", source = "financier.tin")
    void mapCooperateDetailToOrganization(@MappingTarget  OrganizationIdentity organizationIdentity, Financier financier);
}
