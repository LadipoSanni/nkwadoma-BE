package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanReferralMapper {
    @Mapping(target = "loaneeEntity", source = "loanee")
    @Mapping(target = "loaneeEntity.userIdentity", source = "loanee.userIdentity")
    LoanReferralEntity toLoanReferralEntity(LoanReferral loanReferral);

    @InheritInverseConfiguration
    LoanReferral toLoanReferral(LoanReferralEntity savedLoanReferralEntity);

    List<LoanReferral> toLoanReferrals(List<LoanReferralEntity> loanReferralEntities);
}
