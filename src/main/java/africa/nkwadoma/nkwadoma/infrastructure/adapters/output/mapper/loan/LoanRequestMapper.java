package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanRequestMapper {
    @Mapping(target = "loaneeEntity", source = "loanee")
    @Mapping(target = "loaneeEntity.userIdentity", source = "loanee.userIdentity")
    LoanRequestEntity toLoanRequestEntity(LoanRequest loanRequest);

    @InheritInverseConfiguration
    LoanRequest toLoanRequest(LoanRequestEntity loanRequestEntity);
    
    LoanRequest loanRequestProjectionToLoanRequest(LoanRequestProjection loanRequestProjection);

    @Mapping(target = "loanAmountRequested", source = "loanee.loaneeLoanDetail.amountRequested")
    @Mapping(target = "cohortId", source = "loanee.cohortId")
    @Mapping(target = "loanReferralId", source = "id")
    @Mapping(target = "createdDate", expression = "java(java.time.LocalDateTime.now())")
    LoanRequest mapLoanReferralToLoanRequest(LoanReferral updatedLoanReferral);
}
