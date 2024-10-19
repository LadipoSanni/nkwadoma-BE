package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoanProductRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.LoanProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanProductRestMapper {
    @Mapping(source = "totalAmountAvailable", target = "totalAmountAvailable", defaultValue = "0.0")
    @Mapping(source = "totalAmountRepaid", target = "totalAmountRepaid", defaultValue = "0.0")
    @Mapping(source = "totalAmountEarned", target = "totalAmountEarned", defaultValue = "0.0")
    @Mapping(source = "totalAmountDisbursed", target = "totalAmountDisbursed", defaultValue = "0.0")
    @Mapping(source = "minRepaymentAmount", target = "minRepaymentAmount", defaultValue = "0.0")
    @Mapping(source = "obligorLoanLimit", target = "obligorLoanLimit", defaultValue = "0.0")
    LoanProduct mapToLoanProduct(LoanProductRequest request);

    LoanProductResponse mapToLoanProductResponse(LoanProduct createdLoanProduct);
}
