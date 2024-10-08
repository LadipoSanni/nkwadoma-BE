package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.loan;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.CreateLoanProductUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loan.LoanProductRequiredRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.LoanProductResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loan.LoanProductRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.INVITE_ORGANIZATION_SUCCESS;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SwaggerUiConstant.*;

@RestController("/api/v1/loan")
@RequiredArgsConstructor
@Slf4j
public class LoanController {
    private final CreateLoanProductUseCase createLoanProductUseCase;
    private final LoanProductRestMapper loanProductMapper;

    @PostMapping("/create_loan_product")
    @Operation(summary = LOAN_PRODUCT_CREATION,description = LOAN_PRODUCT_CREATION_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> createLoanProduct (@RequestBody @Valid LoanProductRequiredRequest request){
        log.info("Create loan product called.... ");
        LoanProduct loanProduct = loanProductMapper.mapToLoanProduct(request);
        try {
            LoanProduct createdLoanProduct = createLoanProductUseCase.createLoanProduct(loanProduct);
            LoanProductResponse loanProductResponse = loanProductMapper.mapToLoanProductResponse(createdLoanProduct);
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .body(loanProductResponse)
                    .message(INVITE_ORGANIZATION_SUCCESS)
                    .statusCode(HttpStatus.CREATED.toString())
                    .build();
            return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
        } catch (MiddlException e) {
            throw new RuntimeException(e);
        }
    }
}
