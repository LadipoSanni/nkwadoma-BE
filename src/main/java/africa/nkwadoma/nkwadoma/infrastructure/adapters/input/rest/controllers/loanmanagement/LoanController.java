package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanType;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.PaginatedResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.LoanOfferResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;
import java.util.List;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.*;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.ControllerConstant.*;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.LOAN;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.CREATE_LOAN_PRODUCT_SUCCESS;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.cohort.SuccessMessages.COHORT_RETRIEVED;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.loan.SuccessMessages.*;


@RequestMapping( LOAN)
@RequiredArgsConstructor
@RestController
@Slf4j
@Tag(name = LOAN_CONTROLLER, description = LOAN_CONTROLLER_DESCRIPTION)
public class LoanController {
    private final CreateLoanProductUseCase createLoanProductUseCase;
    private final ViewLoanProductUseCase viewLoanProductUseCase;
    private final LoanOfferUseCase loanOfferUseCase;
    private final LoanRestMapper loanRestMapper;
    private final LoanOfferRestMapper loanOfferRestMapper;
    private final LoanProductRestMapper loanProductMapper;
    private final LoaneeLoanAccountRestMapper loaneeLoanAccountRestMapper;
    private final LoanReferralRestMapper loanReferralRestMapper;
    private final LoanMetricsRestMapper loanMetricsRestMapper;

    @PostMapping("/loan-product/create")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
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
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
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
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    @Operation(summary = LOAN_PRODUCT_VIEW_ALL, description = LOAN_PRODUCT_VIEW_ALL_DESCRIPTION )
    public ResponseEntity<ApiResponse<?>> viewAllLoanProduct(@AuthenticationPrincipal Jwt meedl,
                                                             @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                             @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) {
        LoanProduct loanProduct = new LoanProduct();
        loanProduct.setPageSize(pageSize);
        loanProduct.setPageNumber(pageNumber);
        Page<LoanProduct> loanProductPage = viewLoanProductUseCase.viewAllLoanProduct(loanProduct);
        List<LoanProductResponse> loanProductResponses = loanProductPage.stream().map(loanProductMapper::mapToLoanProductResponse).toList();
        PaginatedResponse<LoanProductResponse> paginatedResponse = new PaginatedResponse<>(
                loanProductResponses, loanProductPage.hasNext(), loanProductPage.getTotalPages(),loanProductPage.getTotalElements() , pageNumber,pageSize);
        log.info("View all loan products called successfully.");

        return new ResponseEntity<>(ApiResponse.builder().
                statusCode(HttpStatus.OK.toString()).
                data(paginatedResponse).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL).
                build(), HttpStatus.OK
        );
    }
    @GetMapping("/loan-product/search")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> searchLoanProduct(
            @RequestParam @NotBlank(message = "Loan product name is required") String loanProductName,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {
        Page<LoanProduct> loanProducts = viewLoanProductUseCase.search(loanProductName,pageSize,pageNumber);
        List<LoanProductResponse> loanProductResponses = loanProducts.stream().map(
                 loanProductMapper::mapToLoanProductResponse).toList();
        PaginatedResponse<LoanProductResponse> paginatedResponse = new PaginatedResponse<>(
                loanProductResponses, loanProducts.hasNext(),
                loanProducts.getTotalPages(),loanProducts.getTotalElements() ,pageNumber, pageSize
        );
        ApiResponse<PaginatedResponse<LoanProductResponse>> apiResponse = ApiResponse.<PaginatedResponse<LoanProductResponse>>builder()
                .data(paginatedResponse)
                .message(COHORT_RETRIEVED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @GetMapping("/loan-product/view-details-by-id")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
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
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    @GetMapping("/loan-disbursals/{loanId}")
    public ResponseEntity<ApiResponse<?>> viewLoanDetailsById (
            @PathVariable @NotBlank(message = "Provide a valid loan ID")
            String loanId) throws MeedlException {
        log.info("View loan details by id was called.... {}", loanId);
        Loan loan = createLoanProductUseCase.viewLoanDetails(loanId);
        log.info("Loan details: {}", loan);
        LoanQueryResponse loanResponse = loanRestMapper.toLoanQueryResponse(loan);
        log.info("Loan details response: {}", loanResponse);
        ApiResponse<LoanQueryResponse> apiResponse = ApiResponse.<LoanQueryResponse>builder()
                .data(loanResponse)
                .message(LOAN_DISBURSALS_RETURNED_SUCCESSFULLY)
                .statusCode(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("start")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    @Operation(summary = START_LOAN, description = START_LOAN_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> startLoan(@RequestParam @NotBlank(message = "Loanee ID is required")
                                                    String loaneeId,
                                                    @RequestParam @NotBlank(message = "Loan offer ID is required")
                                                    String loanOfferId) throws MeedlException {
        log.info("Start loan called.... loan offer id : {}", loanOfferId);
        Loan loan = new Loan();
        loan.setLoaneeId(loaneeId);
        loan.setLoanOfferId(loanOfferId);
        loan = createLoanProductUseCase.startLoan(loan);
        StartLoanResponse startLoanResponse = loanProductMapper.toStartLoanResponse(loan);
        ApiResponse<StartLoanResponse> apiResponse = ApiResponse.<StartLoanResponse>builder()
                .data(startLoanResponse)
                .message(SuccessMessages.LOAN_START_SUCCESS)
                .statusCode(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/loan-disbursals")
    @Operation(summary = "View all loan disbursals by organization ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "Loan disbursals retrieved", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoanQueryResponse.class))
            }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Organization not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                    description = "Invalid organization ID provided")
    })
    public ResponseEntity<ApiResponse<PaginatedResponse<LoanQueryResponse>>>
    viewAllLoansByOrganizationId(@Valid @RequestParam(name = "organizationId") @Parameter(description = "id of organization to be searched")
                                 @NotBlank(message = "Organization ID is required") String organizationId,
                                 @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                 @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {
        Loan loan = Loan.builder().organizationId(organizationId).pageNumber(pageNumber).pageSize(pageSize).build();
        Page<Loan> loans = createLoanProductUseCase.viewAllLoansByOrganizationId(loan);
        log.info("Mapped Loan responses: {}", loans.getContent().toArray());
        Page<LoanQueryResponse> loanResponses = loans.map(loanRestMapper::toLoanQueryResponse);
        log.info("Mapped Loan responses: {}", loanResponses.getContent().toArray());
        PaginatedResponse<LoanQueryResponse> paginatedResponse =
                PaginatedResponse.<LoanQueryResponse>builder()
                        .body(loanResponses.getContent())
                        .pageSize(pageSize)
                        .pageNumber(pageNumber)
                        .totalPages(loanResponses.getTotalPages())
                        .hasNextPage(loanResponses.hasNext())
                        .build();
        return ResponseEntity.ok(new ApiResponse<>
                (SuccessMessages.LOAN_DISBURSALS_RETURNED_SUCCESSFULLY, paginatedResponse, HttpStatus.OK.name(), LocalDateTime.now()));
    }

    @PostMapping("/accept/loan-offer")
    @PreAuthorize("hasRole('LOANEE')")
    public ResponseEntity<ApiResponse<?>> acceptLoanOffer(@AuthenticationPrincipal Jwt meedlUser,
                                                          @Valid @RequestBody LoanOfferAcceptRequest loanOfferRequest) throws MeedlException {
        log.info("process of accept loan offer request started: {}", loanOfferRequest);
        LoanOffer loanOffer = loanOfferRestMapper.toLoanOffer(loanOfferRequest);
        loanOffer.setUserId(meedlUser.getClaimAsString("sub"));
        log.info("about to get into the loan offer service : {}", loanOfferRequest);
        LoaneeLoanAccount loaneeLoanAccount  = loanOfferUseCase.acceptLoanOffer(loanOffer);
        LoaneeLoanAccountResponse loaneeLoanAccountResponse = loaneeLoanAccountRestMapper.
                toLoaneeLoanAccountResponse(loaneeLoanAccount);
        ApiResponse<LoaneeLoanAccountResponse> apiResponse = ApiResponse.<LoaneeLoanAccountResponse>builder()
                .data(loaneeLoanAccountResponse)
                .message(ACCEPT_LOAN_OFFER)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @GetMapping("/loanOffer/all")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('LOANEE')")
    public ResponseEntity<ApiResponse<?>> viewLoanOffers(@AuthenticationPrincipal Jwt meedlUser,
                                                         @RequestParam(required = false) String organizationId ,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                         @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {

        Page<LoanOffer> loanOffers = loanOfferUseCase.viewAllLoanOffers(meedlUser.getClaimAsString("sub"),pageSize,pageNumber,organizationId);
        List<AllLoanOfferResponse> loanOfferResponses =  loanOfferRestMapper.toLoanOfferResponses(loanOffers);
        PaginatedResponse<AllLoanOfferResponse> paginatedResponse = new PaginatedResponse<>(
                loanOfferResponses,loanOffers.hasNext(),loanOffers.getTotalPages(),loanOffers.getTotalElements() ,pageNumber,pageSize
        );
        ApiResponse<PaginatedResponse<AllLoanOfferResponse>> apiResponse = ApiResponse.<PaginatedResponse<AllLoanOfferResponse>>builder()
                .data(paginatedResponse)
                .message(ALL_LOAN_OFFERS)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    @GetMapping("/view-loan-offer/{loanOfferId}")
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


    @GetMapping("/search-loan")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> searchLoan(@RequestParam @NotBlank(message = "Program id is required") String programId,
                                                     @RequestParam @NotBlank(message = "Organization id is required") String organizationId,
                                                     @RequestParam LoanType status,
                                                     @RequestParam @NotBlank(message = "Loanee name is required") String name,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                     @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {
        LoanOffer loanOffer = new LoanOffer();
        loanOffer.setProgramId(programId);
        loanOffer.setOrganizationId(organizationId);
        loanOffer.setType(status);
        loanOffer.setName(name);
        loanOffer.setPageSize(pageSize);
        loanOffer.setPageNumber(pageNumber);
        Page<LoanDetail> loanDetails = loanOfferUseCase.searchLoan(loanOffer);
        List<LoanDetailsResponse> loanDetailsResponses = loanMetricsRestMapper.toLoanLifeCycleResponses(loanDetails);
        PaginatedResponse<LoanDetailsResponse> paginatedResponse = new PaginatedResponse<>(
                loanDetailsResponses,loanDetails.hasNext(),loanDetails.getTotalPages(),loanDetails.getTotalElements() ,pageNumber,pageSize
        );
        ApiResponse<PaginatedResponse<LoanDetailsResponse>> apiResponse = ApiResponse.<PaginatedResponse<LoanDetailsResponse>>builder()
                .data(paginatedResponse)
                .message(ALL_LOAN)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    @GetMapping("/view-all-disbursal")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> viewAllDisbursedLoan(@RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                               @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {

        Page<Loan> loans = createLoanProductUseCase.viewAllLoans(pageSize,pageNumber);
        log.info("Mapped Loan responses: {}", loans.getContent().toArray());
        Page<LoanQueryResponse> loanResponses = loans.map(loanRestMapper::toLoanQueryResponse);
        log.info("Mapped Loan responses: {}", loanResponses.getContent().toArray());
        PaginatedResponse<LoanQueryResponse> paginatedResponse =
                PaginatedResponse.<LoanQueryResponse>builder()
                        .body(loanResponses.getContent())
                        .pageSize(pageSize)
                        .pageNumber(pageNumber)
                        .totalPages(loanResponses.getTotalPages())
                        .hasNextPage(loanResponses.hasNext())
                        .build();
        return ResponseEntity.ok(new ApiResponse<>
                (SuccessMessages.LOAN_DISBURSALS_RETURNED_SUCCESSFULLY, paginatedResponse, HttpStatus.OK.name(), LocalDateTime.now()));
    }

    @GetMapping("/filter-by-program")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> filterLoanByProgram(@RequestParam @NotBlank(message = "Program id is required") String programId,
                                                              @RequestParam @NotBlank(message = "Organization id is required") String organizationId,
                                                              @RequestParam LoanType type,
                                                              @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                              @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {

        LoanOffer loanOffer = new LoanOffer();
        loanOffer.setProgramId(programId);
        loanOffer.setOrganizationId(organizationId);
        loanOffer.setType(type);
        loanOffer.setPageSize(pageSize);
        loanOffer.setPageNumber(pageNumber);
        Page<LoanDetail> loanDetails = loanOfferUseCase.filterLoanByProgram(loanOffer);
        List<LoanDetailsResponse> loanDetailsResponses = loanMetricsRestMapper.toLoanLifeCycleResponses(loanDetails);
        PaginatedResponse<LoanDetailsResponse> paginatedResponse = new PaginatedResponse<>(
                loanDetailsResponses,loanDetails.hasNext(),loanDetails.getTotalPages(),loanDetails.getTotalElements() ,pageNumber,pageSize
        );
        ApiResponse<PaginatedResponse<LoanDetailsResponse>> apiResponse = ApiResponse.<PaginatedResponse<LoanDetailsResponse>>builder()
                .data(paginatedResponse)
                .message(ALL_LOAN)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }


}
