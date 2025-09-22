package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.*;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook.LoanUseCase;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanOfferStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanType;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.PaginatedResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.LoanOfferResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanee.LoaneeLoanAccountResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.*;
import io.swagger.v3.oas.annotations.*;
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
    private final LoanUseCase loanUseCase;
    private final LoanOfferUseCase loanOfferUseCase;
    private final LoanRestMapper loanRestMapper;
    private final LoanOfferRestMapper loanOfferRestMapper;
    private final LoanProductRestMapper loanProductMapper;
    private final LoaneeLoanAccountRestMapper loaneeLoanAccountRestMapper;
    private final LoanReferralRestMapper loanReferralRestMapper;
    private final LoanMetricsRestMapper loanMetricsRestMapper;

    @PostMapping("/loan-product/create")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    @Operation(summary = LOAN_PRODUCT_CREATION,description = LOAN_PRODUCT_CREATION_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> createLoanProduct (@AuthenticationPrincipal Jwt meedlUser,
                                                             @RequestBody @Valid LoanProductRequest request) throws MeedlException {
        log.info("Create loan product called....");
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
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
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
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
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
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
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
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_ADMIN')")
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
        Loan loan = loanUseCase.viewLoanDetails(loanId);
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
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    @Operation(summary = START_LOAN, description = START_LOAN_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> startLoan(@RequestParam @NotBlank(message = "Loanee ID is required")
                                                    String loaneeId,
                                                    @RequestParam @NotBlank(message = "Loan offer ID is required")
                                                    String loanOfferId) throws MeedlException {
        log.info("Start loan called.... loan offer id : {}", loanOfferId);
        Loan loan = new Loan();
        loan.setLoaneeId(loaneeId);
        loan.setLoanOfferId(loanOfferId);
        loan = loanUseCase.startLoan(loan);
        log.info("---> Loan object from controller---.> {}", loan);
        StartLoanResponse startLoanResponse = loanProductMapper.toStartLoanResponse(loan);
        ApiResponse<StartLoanResponse> apiResponse = ApiResponse.<StartLoanResponse>builder()
                .data(startLoanResponse)
                .message(SuccessMessages.LOAN_START_SUCCESS)
                .statusCode(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/accept/loan-offer")
    @PreAuthorize("hasRole('LOANEE')")
    public ResponseEntity<ApiResponse<?>> acceptLoanOffer(@AuthenticationPrincipal Jwt meedlUser,
                                                          @Valid @RequestBody LoanOfferAcceptRequest loanOfferRequest) throws MeedlException {
        log.info("process of accept loan offer request started: {}", loanOfferRequest);
        LoanOffer loanOffer = loanOfferRestMapper.toLoanOffer(loanOfferRequest);
        loanOffer.setUserId(meedlUser.getClaimAsString("sub"));
        log.info("about to get into the loan offer service : {}", loanOfferRequest);
        LoaneeLoanAccount loaneeLoanAccount  = loanOfferUseCase.acceptLoanOffer(loanOffer, OnboardingMode.EMAIL_REFERRED);
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
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('ORGANIZATION_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER')" +
            " or hasRole('LOANEE') or hasRole('MEEDL_SUPER_ADMIN') or hasRole('MEEDL_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
    public ResponseEntity<ApiResponse<?>> viewLoanOffers(@AuthenticationPrincipal Jwt meedlUser,
                                                         @RequestParam(required = false) String organizationId ,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                         @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {

        LoanOffer loanOffer = new LoanOffer();
        loanOffer.setUserId(meedlUser.getClaimAsString("sub"));
                loanOffer.setOrganizationId(organizationId);
        loanOffer.setPageNumber(pageNumber);
        loanOffer.setPageSize(pageSize);
        Page<LoanOffer> loanOffers = loanOfferUseCase.viewAllLoanOffers(loanOffer);
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
    @PreAuthorize("hasRole('LOANEE') or hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_SUPER_ADMIN') or hasRole('MEEDL_ADMIN')")
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

    @GetMapping("/view-all-disbursal")
    @PreAuthorize("hasRole('LOANEE') or hasRole('MEEDL_SUPER_ADMIN') or hasRole('MEEDL_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
    public ResponseEntity<ApiResponse<?>> viewAllDisbursedLoan(@RequestParam(required = false) String organizationId,
                                                               @RequestParam(required = false) String loaneeId,
                                                               @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                               @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
                                                               @AuthenticationPrincipal Jwt meedlUser) throws MeedlException {

        log.info("page number == {} pageSize == {} ", pageNumber, pageSize);
        Loan loan = Loan.builder().actorId(meedlUser.getClaimAsString("sub")).organizationId(organizationId).loaneeId(loaneeId)
                .pageNumber(pageNumber).pageSize(pageSize).build();
        Page<Loan> loans = loanUseCase.viewAllLoans(loan);
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
        ApiResponse<PaginatedResponse<LoanQueryResponse>> apiResponse = ApiResponse.<PaginatedResponse<LoanQueryResponse>>builder()
                .data(paginatedResponse)
                .message(ALL_LOAN)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    @GetMapping("/total")
    @PreAuthorize("hasRole('LOANEE') or hasRole('MEEDL_SUPER_ADMIN') or hasRole('MEEDL_ADMIN') or hasRole('ORGANIZATION_SUPER_ADMIN') " +
                  "or hasRole('PORTFOLIO_MANAGER') or hasRole('ORGANIZATION_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
    public ResponseEntity<ApiResponse<?>> viewTotalAmount(@AuthenticationPrincipal Jwt  meedlUser,
                                                          @RequestParam(required = false, name = "loaneeId") String loaneeId) throws MeedlException {

        LoanDetailSummary loanDetailSummary = loanUseCase.viewLoanTotal(meedlUser.getClaimAsString("sub"),loaneeId);
        LoanDetailSummaryResponse loanDetailSummaryResponse = loanRestMapper.toLoanSummaryDetail(loanDetailSummary);
        ApiResponse<LoanDetailSummaryResponse> apiResponse = ApiResponse.<LoanDetailSummaryResponse>builder()
                .data(loanDetailSummaryResponse)
                .message(LOAN_AMOUNT_SUMMARY)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }


    @GetMapping("/filter-by-program")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('MEEDL_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> filterLoanByProgram(@RequestParam @NotBlank(message = "Program id is required") String programId,
                                                              @RequestParam @NotBlank(message = "Organization id is required") String organizationId,
                                                              @RequestParam LoanType type,
                                                              @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                              @RequestParam(name =  "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {

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

    @GetMapping("/search/loanee")
    @PreAuthorize("hasRole('LOANEE') or hasRole('MEEDL_SUPER_ADMIN') or hasRole('MEEDL_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> searchDisbursedLoan(@RequestParam(name = "organizationName") String organizationName ,
                                                              @RequestParam(name = "loaneeId") String loaneeId,
                                                              @AuthenticationPrincipal Jwt meedlUser,
                                                              @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                              @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {


        Loan loan = Loan.builder().actorId(meedlUser.getClaimAsString("sub")).loaneeId(loaneeId).organizationName(organizationName)
                .pageNumber(pageNumber).pageSize(pageSize).build();
        Page<Loan> loans = loanUseCase.searchDisbursedLoan(loan);
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
        ApiResponse<PaginatedResponse<LoanQueryResponse>> apiResponse = ApiResponse.<PaginatedResponse<LoanQueryResponse>>builder()
                .data(paginatedResponse)
                .message(ALL_LOAN)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }



    @GetMapping("/view/loan-referrals")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
    public ResponseEntity<ApiResponse<?>> viewAllLoanReferrals(@RequestParam(name = "programId", required = false) String programId,
                                                               @RequestParam(name = "organizationId" , required = false) String organizationId,
                                                               @RequestParam(defaultValue = "10") int pageSize,
                                                               @RequestParam(defaultValue = "0") int pageNumber) throws MeedlException {

        LoanReferral request = LoanReferral.builder().programId(programId).organizationId(organizationId)
                .pageNumber(pageNumber).pageSize(pageSize).build();
        log.info("request that got in ----- ProgramID == {}  organizationID == {}",request.getProgramId(),request.getOrganizationId());
        Page<LoanReferral> loanReferrals = loanUseCase.viewAllLoanReferrals(request);
        Page<AllLoanReferralResponse> allLoanReferralResponses =
                loanReferrals.map(loanReferralRestMapper::allLoanReferralResponse);

        PaginatedResponse<AllLoanReferralResponse> paginatedResponse =
                PaginatedResponse.<AllLoanReferralResponse>builder()
                        .body(allLoanReferralResponses.getContent())
                        .pageSize(pageSize)
                        .pageNumber(pageNumber)
                        .totalPages(allLoanReferralResponses.getTotalPages())
                        .hasNextPage(allLoanReferralResponses.hasNext())
                        .build();
        ApiResponse<PaginatedResponse<AllLoanReferralResponse>> apiResponse = ApiResponse.<PaginatedResponse<AllLoanReferralResponse>>builder()
                .data(paginatedResponse)
                .message(ALL_LOAN)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }


    @GetMapping("/search/loan-referrals")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
    public ResponseEntity<ApiResponse<?>> searchLoanReferrals(@RequestParam(name = "name") String name,
                                                              @RequestParam(name = "programId", required = false) String programId,
                                                              @RequestParam(name = "organizationId" , required = false) String organizationId,
                                                              @RequestParam(defaultValue = "10") int pageSize,
                                                              @RequestParam(defaultValue = "0") int pageNumber) throws MeedlException {

        LoanReferral request = LoanReferral.builder().name(name).programId(programId).organizationId(organizationId)
                .pageNumber(pageNumber).pageSize(pageSize).build();
        log.info("request that got in ----- ProgramID == {}  organizationID == {}",request.getProgramId(),request.getOrganizationId());
        Page<LoanReferral> loanReferrals = loanUseCase.searchLoanReferrals(request);
        Page<AllLoanReferralResponse> allLoanReferralResponses =
                loanReferrals.map(loanReferralRestMapper::allLoanReferralResponse);

        PaginatedResponse<AllLoanReferralResponse> paginatedResponse =
                PaginatedResponse.<AllLoanReferralResponse>builder()
                        .body(allLoanReferralResponses.getContent())
                        .pageSize(pageSize)
                        .pageNumber(pageNumber)
                        .totalPages(allLoanReferralResponses.getTotalPages())
                        .hasNextPage(allLoanReferralResponses.hasNext())
                        .build();
        ApiResponse<PaginatedResponse<AllLoanReferralResponse>> apiResponse = ApiResponse.<PaginatedResponse<AllLoanReferralResponse>>builder()
                .data(paginatedResponse)
                .message(ALL_LOAN)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }


    @PostMapping("/withdraw/loan-offer")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER') ")
    public ResponseEntity<ApiResponse<?>> withdrawLoanOffer(@RequestParam(name = "loanOfferId") String loanOfferId,
                                                            @RequestParam(name = "loanOfferStatus")LoanOfferStatus loanOfferStatus) throws MeedlException {

        LoanOffer loanOffer = loanOfferUseCase.withdrawLoanOffer(loanOfferId,loanOfferStatus);
        WithDrawLoanOfferResponse withDrawLoanOfferResponse = loanOfferRestMapper.toWithDrawnLoanOfferResponse(loanOffer);
        ApiResponse<WithDrawLoanOfferResponse> apiResponse = ApiResponse.<WithDrawLoanOfferResponse>builder()
                .data(withDrawLoanOfferResponse)
                .message(ControllerConstant.RESPONSE_IS_SUCCESSFUL)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }


}
