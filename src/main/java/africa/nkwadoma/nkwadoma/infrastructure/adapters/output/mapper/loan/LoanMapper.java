package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanMapper {
    @Mapping(target = "loaneeEntity", source = "loanee")
    LoanEntity mapToLoanEntity(Loan loan);

    @InheritInverseConfiguration
    @Mapping(target = "loaneeId", source = "loaneeEntity.id")
    Loan mapToLoan(LoanEntity loanEntity);

    @Mapping(target = "amountRequested", source = "loanAmountRequested")
    @Mapping(target = "loaneeId", source = "loaneeId")
    Loan mapProjectionToLoan(LoanProjection loanProjection);
}
