package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.investment;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentvehicle.FinancierUseCase;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.financier.FinancierVehicleDetail;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentSummary;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.InviteFinancierRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.KycRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.FinancierRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.PaginatedResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.QAResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.invesmentvehicle.FinancierRestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.invesmentvehicle.InvestmentVehicleRestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.swagger.SwaggerDocumentation;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.swaggerannotationdoc.FinancierDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.swaggerannotationdoc.FinancierInvestmentDetailDocs;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.constants.ControllerConstant;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.constants.ControllerConstant.RETURNED_SUCCESSFULLY;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FinancierController {
    private final FinancierUseCase financierUseCase;
    private final FinancierRestMapper financierRestMapper;
    private final InvestmentVehicleRestMapper investmentVehicleRestMapper;

    @PostMapping("financier/invite")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_ADMIN') or hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
    public  ResponseEntity<ApiResponse<?>> inviteFinancierToVehicle(@AuthenticationPrincipal Jwt meedlUser, @RequestBody @Valid
    InviteFinancierRequest inviteFinancierRequest) throws MeedlException {
        log.info("Inviting a financier with request {}", inviteFinancierRequest);
        List<Financier> financiers = mapValues(meedlUser.getClaimAsString("sub"), inviteFinancierRequest.getFinancierRequests());
        log.info("Mapped financier at controller {}", financiers);
        String message = financierUseCase.inviteFinancier(financiers, inviteFinancierRequest.getInvestmentVehicleId());

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message(message)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    private List<Financier> mapValues(String meedlUserId, List<FinancierRequest> financierRequests) throws MeedlException {
        MeedlValidator.validateObjectInstance(financierRequests, "The list of financier is missing.");
        validateFinanciers(financierRequests);
        return financierRequests.stream().map(financierRequest ->{
            Financier financier = financierRestMapper.map(financierRequest);
            if (financierRequest.getFinancierType() == FinancierType.COOPERATE){
                financier = financierRestMapper.mapToCooperateFinancier(financierRequest);
            } else if (financierRequest.getFinancierType() == FinancierType.INDIVIDUAL) {
                financier.setUserIdentity(financierRequest.getUserIdentity());
            }
            financier.getUserIdentity().setCreatedBy(meedlUserId);
            financier.getUserIdentity().setCreatedAt(LocalDateTime.now());
            return financier;
        }).toList();
    }

    private void validateFinanciers(List<FinancierRequest> financierRequests) throws MeedlException {
        for (FinancierRequest financierRequest : financierRequests){
            MeedlValidator.validateObjectInstance(financierRequest, "Financier invite request cannot be empty");
            MeedlValidator.validateObjectInstance(financierRequest.getUserIdentity(), "Financier user detail cannot be empty");
            MeedlValidator.validateObjectInstance(financierRequest.getFinancierType(), "Financier type must be provided");
        }
    }


    @PostMapping("financier/complete-kyc")
    @PreAuthorize("hasRole('FINANCIER') or  hasRole('COOPERATE_FINANCIER_SUPER_ADMIN') or hasRole('COOPERATE_FINANCIER_ADMIN')")
    public ResponseEntity<ApiResponse<?>> completeKyc(@AuthenticationPrincipal Jwt meedlUser,
                                                      @RequestBody KycRequest kycRequest) throws MeedlException {
        log.info("Kyc request controller {} , {}",LocalDateTime.now(), kycRequest);
        Financier financier = financierRestMapper.map(kycRequest);
        financier.getUserIdentity().setId(meedlUser.getClaimAsString("sub"));
        log.info("Controller request for kyc mapped {}", financier);
        financier = financierUseCase.completeKyc(financier);

        KycResponse kycResponse = financierRestMapper.mapToFinancierResponse(financier);
        ApiResponse<KycResponse> apiResponse = ApiResponse.<KycResponse>builder()
                .data(kycResponse)
                .message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage())
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    @PostMapping("financier/vehicle/invest")
    @PreAuthorize("hasRole('FINANCIER') or hasRole('COOPERATE_FINANCIER_SUPER_ADMIN') or hasRole('COOPERATE_FINANCIER_ADMIN')")
    @Operation(
            summary = SwaggerDocumentation.INVEST_IN_VEHICLE_SUMMARY,
            description = SwaggerDocumentation.INVEST_IN_VEHICLE_DESCRIPTION
    )
    public ResponseEntity<ApiResponse<?>> investInVehicle(@AuthenticationPrincipal Jwt meedlUser, @RequestBody FinancierRequest financierRequest) throws MeedlException {
        Financier financier = financierRestMapper.map(financierRequest, meedlUser.getClaimAsString("sub"));
        financier = financierUseCase.investInVehicle(financier);

        KycResponse kycResponse = financierRestMapper.mapToFinancierResponse(financier);
        ApiResponse<KycResponse> apiResponse = ApiResponse.<KycResponse>builder()
                .data(kycResponse)
                .message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage())
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("financier/view/investment-detail/{financierId}")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('FINANCIER')")
    @FinancierInvestmentDetailDocs
    public ResponseEntity<ApiResponse<?>> viewInvestmentDetailOfFinancier(@PathVariable(required = false) String financierId, @AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        String userId = meedlUser.getClaimAsString("sub");
        FinancierVehicleDetail financierVehicleDetail = financierUseCase.viewInvestmentDetailOfFinancier(financierId, userId);
        FinancierInvestmentDetailResponse financierInvestmentDetailResponse = financierRestMapper.mapToFinancierDetailResponse(financierVehicleDetail);

        ApiResponse<FinancierInvestmentDetailResponse> apiResponse = ApiResponse.<FinancierInvestmentDetailResponse>builder()
                .data(financierInvestmentDetailResponse)
                .message(ControllerConstant.VIEW_EMPLOYEE_DETAILS_SUCCESSFULLY.getMessage())
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    @PostMapping("financier/privacy-policy-decision")
    @PreAuthorize("hasRole('FINANCIER')")
    public ResponseEntity<ApiResponse<?>> makePrivacyPolicyDecision(@AuthenticationPrincipal Jwt meedlUser, @RequestParam boolean privacyPolicyDecision) throws MeedlException {
        Financier financier = financierRestMapper.map(meedlUser.getClaimAsString("sub"), privacyPolicyDecision);
        String response = financierUseCase.makePrivacyPolicyDecision(financier);

        ApiResponse<FinancierInvestmentDetailResponse> apiResponse = ApiResponse.<FinancierInvestmentDetailResponse>builder()
                .message(response)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("financier/respond-to-financier-invite")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<?>> respondToFinancierInvite(@AuthenticationPrincipal Jwt meedlUser,
                                                                   @RequestParam String financierId,
                                                                   @RequestParam ActivationStatus activationStatus) throws MeedlException {
        Financier financier = financierRestMapper.map(meedlUser.getClaimAsString("sub"), financierId, activationStatus);
        financier = financierUseCase.respondToFinancierInvite(financier);
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .statusCode(HttpStatus.OK.toString())
                .message(financier.getResponse())
                .data(QAResponse.builder().id(financier.getId()).email(financier.getUserIdentity().getEmail()).build())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);

    }

    @GetMapping("financier/view")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') " +
            "or hasRole('PORTFOLIO_MANAGER') " +
            "or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')"+
            "or hasRole('FINANCIER')" +
            "or hasRole('COOPERATE_FINANCIER_ADMIN')" +
            "or hasRole('COOPERATE_FINANCIER_SUPER_ADMIN')"
    )
    @FinancierDetail
    public ResponseEntity<ApiResponse<?>>viewFinancierDetail(@AuthenticationPrincipal Jwt meedlUser,@RequestParam(required = false) String financierId) throws MeedlException {
        String userId = meedlUser.getClaimAsString("sub");
        Financier financier = financierUseCase.viewFinancierDetail(userId, financierId);
        FinancierDashboardResponse financierDashboardResponse = financierRestMapper.mapToDashboardResponse(financier);

        ApiResponse<FinancierDashboardResponse> apiResponse = ApiResponse.<FinancierDashboardResponse>builder()
                .message(RETURNED_SUCCESSFULLY.getMessage())
                .data(financierDashboardResponse)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("financier/all/view")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER')  or hasRole('MEEDL_ADMIN') " +
            "or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
    public  ResponseEntity<ApiResponse<?>> viewAllFinancier(@AuthenticationPrincipal Jwt meedlUser,
                                                            @RequestParam int pageNumber,
                                                            @RequestParam int pageSize,
                                                            @RequestParam(required = false) String investmentVehicleId,
                                                            @RequestParam(required = false) FinancierType financierType,
                                                            @RequestParam(required = false) List<ActivationStatus> activationStatuses) throws MeedlException {
        Financier financier = Financier.builder().pageNumber(pageNumber).pageSize(pageSize).investmentVehicleId(investmentVehicleId).financierType(financierType).activationStatus(activationStatuses.get(0)).build();
        Page<Financier> financiers = financierUseCase.viewAllFinancier(financier);
        List<FinancierResponse > financierResponses = financiers.stream().map(financierRestMapper::map).toList();
        log.info("financiers mapped for view all financiers on the platform: {}", financierResponses);
        PaginatedResponse<FinancierResponse> response = new PaginatedResponse<>(
                financierResponses, financiers.hasNext(),
                financiers.getTotalPages(),financiers.getTotalElements(), pageNumber, pageSize
        );
        return new ResponseEntity<>(ApiResponse.builder().
                statusCode(HttpStatus.OK.toString()).
                data(response).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                build(), HttpStatus.OK
        );
    }
    @GetMapping("financier/search")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER')  or hasRole('MEEDL_ADMIN') " +
            "or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
    public  ResponseEntity<ApiResponse<?>> search(@AuthenticationPrincipal Jwt meedlUser,
                                                  @RequestParam String name,
                                                  @RequestParam int pageNumber,
                                                  @RequestParam int pageSize,
                                                  @RequestParam(required = false) FinancierType financierType,
                                                  @RequestParam(required = false) ActivationStatus activationStatus,
                                                  @RequestParam(required = false) String investmentVehicleId
    ) throws MeedlException {
        Financier financier = Financier.builder().pageSize(pageSize).pageNumber(pageNumber).financierType(financierType).investmentVehicleId(investmentVehicleId).activationStatus(activationStatus).build();
        Page<Financier> financiers = financierUseCase.search(name, financier);
        List<FinancierResponse> financierResponses = financiers.stream().map(financierRestMapper::map).toList();
        log.info("Found financiers for search financier: {}", financiers);
        PaginatedResponse<FinancierResponse> response = new PaginatedResponse<>(
                financierResponses, financiers.hasNext(),
                financiers.getTotalPages(),financiers.getTotalElements(), pageNumber, pageSize
        );

        return new ResponseEntity<>(ApiResponse.builder().
                statusCode(HttpStatus.OK.toString()).
                data(response).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                build(), HttpStatus.OK
        );
    }


    @GetMapping("financier/investment-detail")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('FINANCIER')")
    @FinancierInvestmentDetailDocs
    public ResponseEntity<ApiResponse<?>> viewFinancierInvestmentDetail(@RequestParam(required = false) String financierId,
                                                                        @RequestParam String investmentVehicleFinancierId,
                                                                        @AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        String userId = meedlUser.getClaimAsString("sub");
        InvestmentSummary investmentSummary = financierUseCase.viewInvestmentDetailOfFinancier(financierId, investmentVehicleFinancierId, userId);
        InvestmentDetailResponse investmentDetailResponse = investmentVehicleRestMapper.toInvestmentDetailResponse(investmentSummary);

        ApiResponse<InvestmentDetailResponse> apiResponse = ApiResponse.<InvestmentDetailResponse>builder()
                .data(investmentDetailResponse)
                .message(ControllerConstant.VIEW_EMPLOYEE_DETAILS_SUCCESSFULLY.getMessage())
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("financier/investment-vehicle/all/view")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER')  or hasRole('MEEDL_ADMIN') " +
            "or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
    public  ResponseEntity<ApiResponse<?>> viewAllFinancierInInvestmentVehicle(@AuthenticationPrincipal Jwt meedlUser,
                                                            @RequestParam int pageNumber,
                                                            @RequestParam int pageSize,
                                                            @RequestParam(required = false) ActivationStatus activationStatus,
                                                            @RequestParam String investmentVehicleId) throws MeedlException {
        Financier financier = Financier.builder().investmentVehicleId(investmentVehicleId).activationStatus(activationStatus).pageNumber(pageNumber).pageSize(pageSize).build();
        Page<Financier> financiers = financierUseCase.viewAllFinancierInInvestmentVehicle(financier);
        List<FinancierResponse> financierResponses = financiers.stream().map(financierRestMapper::map).toList();
        log.info("View all financier in investment vehicle. Financiers mapped: {} in ", financierResponses);
        PaginatedResponse<FinancierResponse> response = new PaginatedResponse<>(
                financierResponses, financiers.hasNext(),
                financiers.getTotalPages(),financiers.getTotalElements(), pageNumber, pageSize
        );
        return new ResponseEntity<>(ApiResponse.builder().
                statusCode(HttpStatus.OK.toString()).
                data(response).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                build(), HttpStatus.OK
        );
    }

    @GetMapping("financier/all-investment")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('FINANCIER') or hasRole('COOPERATE_FINANCIER_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<?>> viewAllFinancierInvestment(@AuthenticationPrincipal Jwt meedlUser,
                                                                     @RequestParam(required = false) String financierId,
                                                                     @RequestParam int pageSize,
                                                                     @RequestParam int pageNumber) throws MeedlException {
        Page<Financier> financierInvestments =
                financierUseCase.viewAllFinancierInvestment(meedlUser.getClaimAsString("sub"),financierId,pageSize,pageNumber);
        List<FinancierInvestmentResponse> financierResponses = financierInvestments.stream().map(financierRestMapper::mapToFinancierInvestment).toList();
        log.info("financiers investment mapped for view all financiers investment on the platform: {}", financierResponses);
        PaginatedResponse<FinancierInvestmentResponse> response = new PaginatedResponse<>(
                financierResponses, financierInvestments.hasNext(),
                financierInvestments.getTotalPages(),financierInvestments.getTotalElements(), pageNumber, pageSize
        );
        return new ResponseEntity<>(ApiResponse.builder().
                statusCode(HttpStatus.OK.toString()).
                data(response).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                build(), HttpStatus.OK
        );
    }

    @GetMapping("financier/search/all/investment/investment-vehicle")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('FINANCIER') or hasRole('COOPERATE_FINANCIER_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<?>> viewAllFinancierInvestment(@AuthenticationPrincipal Jwt meedlUser,
                                                                     @RequestParam String investmentVehicleName,
                                                                     @RequestParam(required = false) String financierId,
                                                                     @RequestParam int pageSize,
                                                                     @RequestParam int pageNumber) throws MeedlException {
        Financier financier = Financier.builder().investmentVehicleName(investmentVehicleName).id(financierId)
                .actorId(meedlUser.getClaimAsString("sub")).pageSize(pageSize).pageNumber(pageNumber).build();
        Page<Financier> financierInvestments =
                financierUseCase.searchFinancierInvestment(financier);
        List<FinancierInvestmentResponse> financierResponses = financierInvestments.stream().map(financierRestMapper::mapToFinancierInvestment).toList();
        log.info("financiers investment mapped for search financiers investment on the platform: {}", financierResponses);
        PaginatedResponse<FinancierInvestmentResponse> response = new PaginatedResponse<>(
                financierResponses, financierInvestments.hasNext(),
                financierInvestments.getTotalPages(),financierInvestments.getTotalElements(), pageNumber, pageSize
        );
        return new ResponseEntity<>(ApiResponse.builder().
                statusCode(HttpStatus.OK.toString()).
                data(response).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                build(), HttpStatus.OK
        );
    }

    @GetMapping("financier/{email}")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_ADMIN') or hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
    public ResponseEntity<ApiResponse<?>> viewFinancierByEmail(@PathVariable String email) throws MeedlException {
        Financier financier = financierUseCase.viewFinancierByEmail(email);
        FinancierResponse financierResponse = financierRestMapper.map(financier);

        ApiResponse<FinancierResponse> apiResponse = ApiResponse.<FinancierResponse>builder()
                .data(financierResponse)
                .message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage())
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


}
