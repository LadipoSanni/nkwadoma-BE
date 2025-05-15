package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanBook;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoanProductRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.LoanProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.ControllerConstant.*;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.CREATE_LOAN_PRODUCT_SUCCESS;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.*;

@RequestMapping(BASE_URL + LOAN_BOOK)
@RequiredArgsConstructor
@RestController
@Slf4j
@Tag(name = LOAN_BOOK_CONTROLLER, description = LOAN_BOOK_CONTROLLER_DESCRIPTION)
public class LoanBookController {

    @PostMapping("/upload")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    @Operation(summary = LOAN_PRODUCT_CREATION,description = LOAN_PRODUCT_CREATION_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> createLoanProduct (@AuthenticationPrincipal Jwt meedlUser, @RequestBody @Valid LoanBookRequest request) throws MeedlException {
        log.info("Upload loan book. Api .... ");
        LoanProduct loanProduct = loanProductMapper.mapToLoanProduct(request);
        loanProduct.setCreatedBy(meedlUser.getClaimAsString("sub"));
        LoanProduct createdLoanProduct = createLoanProductUseCase.createLoanProduct(loanProduct);
        LoanProductResponse loanProductResponse = loanProductMapper.mapToLoanProductResponse(createdLoanProduct);
        ApiResponse<LoanProductResponse> apiResponse = ApiResponse.<LoanProductResponse>builder()
                .data(loanProductResponse)
                .message(CREATE_LOAN_PRODUCT_SUCCESS)
                .statusCode(HttpStatus.CREATED.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
    }
}
