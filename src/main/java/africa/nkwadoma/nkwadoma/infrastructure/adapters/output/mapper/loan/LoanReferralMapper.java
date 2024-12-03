package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import org.mapstruct.*;

import java.util.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanReferralMapper {
    @Mapping(target = "loaneeEntity", source = "loanee")
    @Mapping(target = "loaneeEntity.userIdentity", source = "loanee.userIdentity")
    LoanReferralEntity toLoanReferralEntity(LoanReferral loanReferral);

    @InheritInverseConfiguration
    LoanReferral toLoanReferral(LoanReferralEntity savedLoanReferralEntity);

    @Mapping(target = "loanee.userIdentity.id", source = "id")
    @Mapping(target = "loanee.userIdentity.lastName", source = "lastName")
    @Mapping(target = "loanee.userIdentity.firstName", source = "firstName")
    LoanReferral mapProjectionToLoanReferralEntity(LoanReferralProjection loanReferralProjection);
}
