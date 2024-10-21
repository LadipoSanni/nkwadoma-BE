package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.CreateLoanProductUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loan.ViewLoanProductUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoanProductRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.*;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SwaggerUiConstant.*;

@RestController("/api/v1/loan")
@RequiredArgsConstructor
@Slf4j
public class LoanController {
    private final CreateLoanProductUseCase createLoanProductUseCase;
    private final ViewLoanProductUseCase viewLoanProductUseCase;
    private final LoanProductRestMapper loanProductMapper;

    @PostMapping("/loan-product/create")
    @Operation(summary = LOAN_PRODUCT_CREATION,description = LOAN_PRODUCT_CREATION_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> createLoanProduct (@RequestBody @Valid LoanProductRequest request) throws MeedlException {
        log.info("Create loan product called with name .... {}", request.getName());
        LoanProduct loanProduct = loanProductMapper.mapToLoanProduct(request);
            LoanProduct createdLoanProduct = createLoanProductUseCase.createLoanProduct(loanProduct);
            LoanProductResponse loanProductResponse = loanProductMapper.mapToLoanProductResponse(createdLoanProduct);
            ApiResponse<LoanProductResponse> apiResponse = ApiResponse.<LoanProductResponse>builder()
                    .body(loanProductResponse)
                    .message(CREATE_LOAN_PRODUCT_SUCCESS)
                    .statusCode(HttpStatus.CREATED.toString())
                    .build();
            return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
    }
    @PostMapping("/loan-product/view-details-by-id")
    @Operation(summary = VIEW_LOAN_PRODUCT_DETAILS,description = VIEW_LOAN_PRODUCT_DETAILS_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> viewLoanProductDetailsById (@RequestParam String loanProductId) throws MeedlException {
        log.info("View loan product details by id was called.... {}", loanProductId);
        LoanProduct createdLoanProduct = viewLoanProductUseCase.viewLoanProductDetailsById(loanProductId);
        LoanProductResponse loanProductResponse = loanProductMapper.mapToLoanProductResponse(createdLoanProduct);
        ApiResponse<LoanProductResponse> apiResponse = ApiResponse.<LoanProductResponse>builder()
                .body(loanProductResponse)
                .message(LOAN_PRODUCT_FOUND_SUCCESSFULLY)
                .statusCode(HttpStatus.FOUND.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.FOUND);
    }
}
