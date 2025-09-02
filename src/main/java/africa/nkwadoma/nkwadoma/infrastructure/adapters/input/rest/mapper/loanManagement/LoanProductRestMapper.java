package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement;

import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.SponsorsRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoanProductRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.LoanProductResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.StartLoanResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanProductRestMapper {
    @Mapping(target = "sponsors", source = "sponsors")
    LoanProduct mapToLoanProduct(LoanProductRequest request);
    // Helper mapping: SponsorsRequest -> Financier
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    Financier toFinancier(SponsorsRequest sponsor);

    // Map list explicitly
    List<Financier> toFinanciers(List<SponsorsRequest> sponsors);

    LoanProductResponse mapToLoanProductResponse(LoanProduct createdLoanProduct);

    StartLoanResponse toStartLoanResponse(Loan loan);

    List<LoanProductResponse> mapToLoanProductResponses(List<LoanProduct> loanProducts);
}
