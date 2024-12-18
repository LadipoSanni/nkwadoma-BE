package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import org.mapstruct.*;


import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanReferralMapper {
    @Mapping(target = "loaneeEntity", source = "loanee")
    @Mapping(target = "loaneeEntity.userIdentity", source = "loanee.userIdentity")
    @Mapping(target = "loaneeEntity.userIdentity.alternateEmail", source = "loanee.userIdentity.alternateEmail")
    @Mapping(target = "loaneeEntity.userIdentity.alternatePhoneNumber", source = "loanee.userIdentity.alternatePhoneNumber")
    @Mapping(target = "loaneeEntity.userIdentity.alternateContactAddress", source = "loanee.userIdentity.alternateContactAddress")
    LoanReferralEntity toLoanReferralEntity(LoanReferral loanReferral);

    @InheritInverseConfiguration
    LoanReferral toLoanReferral(LoanReferralEntity savedLoanReferralEntity);

    @Mapping(target = "loanee.userIdentity.id", source = "id")
    @Mapping(target = "loanee.id", source = "loaneeId")
    @Mapping(target = "loanee.userIdentity.lastName", source = "lastName")
    @Mapping(target = "loanee.userIdentity.firstName", source = "firstName")
    @Mapping(target = "loanee.userIdentity.alternateContactAddress", source = "alternateContactAddress")
    @Mapping(target = "loanee.userIdentity.alternateEmail", source = "alternateEmail")
    @Mapping(target = "loanee.userIdentity.alternatePhoneNumber", source = "alternatePhoneNumber")
    @Mapping(target = "loanReferralStatus", source = "status")
    LoanReferral mapProjectionToLoanReferralEntity(LoanReferralProjection loanReferralProjection);

    List<LoanReferral> toLoanReferrals(List<LoanReferralEntity> loanReferralEntities);
}
