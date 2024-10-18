package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanProductEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanProductMapper {
    @Mapping(source = "amountAvailable", target = "amountAvailable", defaultValue = "0.0")
    @Mapping(source = "amountRepaid", target = "amountRepaid", defaultValue = "0.0")
    @Mapping(source = "amountEarned", target = "amountEarned", defaultValue = "0.0")
    @Mapping(source = "amountDisbursed", target = "amountDisbursed", defaultValue = "0.0")
    @Mapping(source = "minRepaymentAmount", target = "minRepaymentAmount", defaultValue = "0.0")
    @Mapping(source = "obligorLoanLimit", target = "obligorLoanLimit", defaultValue = "0.0")
    LoanProductEntity mapLoanProductToEntity(LoanProduct loanProduct);

    LoanProduct mapEntityToLoanProduct(LoanProductEntity entity);
}
