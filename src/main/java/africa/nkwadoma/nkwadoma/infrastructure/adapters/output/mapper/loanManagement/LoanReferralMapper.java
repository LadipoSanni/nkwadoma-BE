package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import org.mapstruct.*;


import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanReferralMapper {


    LoanReferralEntity toLoanReferralEntity(LoanReferral loanReferral);

    @InheritInverseConfiguration
    @Mapping(target = "cohortLoanee.loanee.userIdentity.isIdentityVerified", source = "cohortLoanee.loanee.userIdentity.identityVerified")
    LoanReferral toLoanReferral(LoanReferralEntity savedLoanReferralEntity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "loanee.id", source = "loaneeId")
    @Mapping(target = "loanee.userIdentity.lastName", source = "lastName")
    @Mapping(target = "loanee.userIdentity.firstName", source = "firstName")
    @Mapping(target = "loanee.userIdentity.email", source = "email")
    @Mapping(target = "loanee.userIdentity.id", source = "userId")
    @Mapping(target = "loanee.userIdentity.alternateContactAddress", source = "alternateContactAddress")
    @Mapping(target = "loanee.userIdentity.alternateEmail", source = "alternateEmail")
    @Mapping(target = "loanee.userIdentity.alternatePhoneNumber", source = "alternatePhoneNumber")
    @Mapping(target = "loanReferralStatus", source = "status")
    @Mapping(target = "identityVerified", source = "identityVerified")
    @Mapping(target = "loanee.userIdentity.isIdentityVerified", source = "identityVerified")
    @Mapping(target = "loaneeUserId", source = "userId")
    @Mapping(target = "referredBy", source = "referredBy")
    @Mapping(target = "cohortLoaneeId", source = "cohortLoaneeId")
    LoanReferral mapProjectionToLoanReferralEntity(LoanReferralProjection loanReferralProjection);

    List<LoanReferral> toLoanReferrals(List<LoanReferralEntity> loanReferralEntities);

    @Mapping(target = "referralDate", source = "referralDateTime")
    LoanReferral mapProjectionToLoanReferral(LoanReferralProjection loanReferralProjection);
}
