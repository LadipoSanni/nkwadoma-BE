package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanReferralMapper {
    @Mapping(target = "loaneeEntity", source = "loanee")
    @Mapping(target = "loaneeEntity.loanee", source = "loanee.userIdentity")
    LoanReferralEntity toLoanReferralEntity(LoanReferral loanReferral);

    @InheritInverseConfiguration
    LoanReferral toLoanReferral(LoanReferralEntity savedLoanReferralEntity);
}
