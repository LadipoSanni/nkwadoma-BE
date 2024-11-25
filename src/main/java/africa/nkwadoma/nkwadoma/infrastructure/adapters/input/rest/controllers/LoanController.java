package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanOfferOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoanProductRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoanProductViewAllRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.PaginatedResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoanOfferMapper;
import africa.nkwadoma.nkwadoma.infrastructure.enums.constants.ControllerConstant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.*;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.ControllerConstant.*;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.LOAN;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.CREATE_LOAN_PRODUCT_SUCCESS;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.loan.SuccessMessages.LOAN_OFFER_FOUND;


@RequestMapping(BASE_URL + LOAN)
@RequiredArgsConstructor
@RestController
@Slf4j
@Tag(name = LOAN_CONTROLLER, description = LOAN_CONTROLLER_DESCRIPTION)
public class LoanController {
    private final CreateLoanProductUseCase createLoanProductUseCase;
    private final ViewLoanProductUseCase viewLoanProductUseCase;
    private final ViewLoanReferralsUseCase viewLoanReferralsUseCase;
    private final LoanProductRestMapper loanProductMapper;
    private final LoanReferralRestMapper loanReferralRestMapper;
    private final LoanOfferUseCase loanOfferUseCase;
    private final LoanOfferRestMapper loanOfferRestMapper;

    @PostMapping("/loan-product/create")
    @PreAuthorize("hasAuthority('PORTFOLIO_MANAGER')")
    @Operation(summary = LOAN_PRODUCT_CREATION,description = LOAN_PRODUCT_CREATION_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> createLoanProduct (@AuthenticationPrincipal Jwt meedlUser, @RequestBody @Valid LoanProductRequest request) throws MeedlException {
        log.info("Create loan product called.... ");
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
    @PostMapping("/loan-product/update")
    @Operation(summary = LOAN_PRODUCT_UPDATE,description = LOAN_PRODUCT_UPDATE_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> updateLoanProduct (@RequestBody LoanProductRequest request) throws MeedlException {
        log.info("Update loan product called with id .... {}", request.getId());
        LoanProduct loanProduct = loanProductMapper.mapToLoanProduct(request);
        LoanProduct updatedLoanProduct = createLoanProductUseCase.updateLoanProduct(loanProduct);
        LoanProductResponse loanProductResponse = loanProductMapper.mapToLoanProductResponse(updatedLoanProduct);
        ApiResponse<LoanProductResponse> apiResponse = ApiResponse.<LoanProductResponse>builder()
                .data(loanProductResponse)
                .message(UPDATED_LOAN_PRODUCT_SUCCESS)
                .statusCode(HttpStatus.CREATED.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
    }
    @GetMapping("/loan-product/all")
    @PreAuthorize("hasAuthority('PORTFOLIO_MANAGER')")
    @Operation(summary = LOAN_PRODUCT_VIEW_ALL, description = LOAN_PRODUCT_VIEW_ALL_DESCRIPTION )
    public ResponseEntity<ApiResponse<?>> viewAllLoanProduct(@Valid @RequestBody LoanProductViewAllRequest request) {
        LoanProduct loanProduct = new LoanProduct();
        loanProduct.setPageSize(request.getPageSize());
        loanProduct.setPageNumber(request.getPageNumber());
        Page<LoanProduct> loanProductPage = viewLoanProductUseCase.viewAllLoanProduct(loanProduct);
        List<LoanProductResponse> loanProductResponses = loanProductPage.stream().map(loanProductMapper::mapToLoanProductResponse).toList();
        log.info("View all loan products called.... {}", loanProductResponses.isEmpty());
        PaginatedResponse<LoanProductResponse> response = new PaginatedResponse<>(
                loanProductResponses, loanProductPage.hasNext(),
                loanProductPage.getTotalPages(), request.getPageSize(),
                request.getPageNumber()
        );
        return new ResponseEntity<>(ApiResponse.builder().
                statusCode(HttpStatus.OK.toString()).
                data(response).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                build(), HttpStatus.OK
        );
    }


    @GetMapping("/loan-product/view-details-by-id")
    @Operation(summary = VIEW_LOAN_PRODUCT_DETAILS,description = VIEW_LOAN_PRODUCT_DETAILS_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> viewLoanProductDetailsById (@RequestParam
                                                                          @NotBlank(message = "Provide a valid loan product identifier")
                                                                          String loanProductId) throws MeedlException {
        log.info("View loan product details by id was called.... {}", loanProductId);
        LoanProduct createdLoanProduct = viewLoanProductUseCase.viewLoanProductDetailsById(loanProductId);
        LoanProductResponse loanProductResponse = loanProductMapper.mapToLoanProductResponse(createdLoanProduct);
        ApiResponse<LoanProductResponse> apiResponse = ApiResponse.<LoanProductResponse>builder()
                .data(loanProductResponse)
                .message(LOAN_PRODUCT_FOUND_SUCCESSFULLY)
                .statusCode(HttpStatus.FOUND.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.FOUND);
    }

    @GetMapping("loan-referral/{loaneeId}")
    public ResponseEntity<ApiResponse<?>> viewLoanReferral (@PathVariable @NotBlank(message = "Loanee ID is required")
                                                                          String loaneeId) throws MeedlException {
        LoanReferral loanReferral = new LoanReferral();
        loanReferral.getLoanee().setId(loaneeId.trim());
        LoanReferral foundLoanReferral = viewLoanReferralsUseCase.viewLoanReferral(loanReferral);
        LoanReferralResponse loanReferralResponse = loanReferralRestMapper.toLoanReferralResponse(foundLoanReferral);
        ApiResponse<LoanReferralResponse> apiResponse = ApiResponse.<LoanReferralResponse>builder()
                .data(loanReferralResponse)
                .message(SuccessMessages.LOAN_REFERRAL_FOUND_SUCCESSFULLY)
                .statusCode(HttpStatus.FOUND.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.FOUND);
    }

    @GetMapping("view-loan-offer/{loanOfferId}")
    @PreAuthorize("hasRole('LOANEE') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> viewLoanOffer(@AuthenticationPrincipal Jwt meedlUser, @PathVariable @NotBlank(message = "LoanOffer ID is required")
                                                            String loanOfferId ) throws MeedlException {
        LoanOffer loanOffer = loanOfferUseCase.viewLoanOfferDetails((meedlUser.getClaimAsString("sub")),loanOfferId);
        LoanOfferResponse loanOfferResponse = loanOfferRestMapper.toLoanOfferResponse(loanOffer);
        ApiResponse<LoanOfferResponse> apiResponse = ApiResponse.<LoanOfferResponse>builder()
                .data(loanOfferResponse)
                .message(LOAN_OFFER_FOUND)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

}
