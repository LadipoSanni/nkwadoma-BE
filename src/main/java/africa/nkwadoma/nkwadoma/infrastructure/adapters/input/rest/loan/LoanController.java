package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.loan;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.CreateLoanProductUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loan.LoanProductRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loan.LoanProductRequiredRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.LoanProductResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loan.LoanProductRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/loan")
@RequiredArgsConstructor
public class LoanController {
    private final CreateLoanProductUseCase createLoanProductUseCase;
    private final LoanProductRestMapper loanProductMapper;

    @PostMapping("/create_loan_product")
    public LoanProductResponse createLoanProduct (@RequestBody @Valid LoanProductRequiredRequest request) throws MiddlException {
        LoanProduct loanProduct = loanProductMapper.mapToLoanProduct(request);
        LoanProduct createdLoanProduct = createLoanProductUseCase.createLoanProduct(loanProduct);
        return loanProductMapper.mapToLoanProductResponse(createdLoanProduct);
    }
}
