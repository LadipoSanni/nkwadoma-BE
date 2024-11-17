package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanRequestMapper {
    @Mapping(target = "loaneeEntity", source = "loanee")
    @Mapping(target = "loaneeEntity.loanee", source = "loanee.userIdentity")
    @Mapping(target = "dateTimeApproved", expression = "java(java.time.LocalDateTime.now())")
    LoanRequestEntity toLoanRequestEntity(LoanRequest loanRequest);

    @InheritInverseConfiguration
    LoanRequest toLoanRequest(LoanRequestEntity loanRequestEntity);

    @Mapping(target = "loanAmountRequested", source = "loanee.loaneeLoanDetail.amountRequested")
    @Mapping(target = "dateTimeApproved", expression = "java(java.time.LocalDateTime.now())")
    LoanRequest mapLoanReferralToLoanRequest(LoanReferral updatedLoanReferral);
}
