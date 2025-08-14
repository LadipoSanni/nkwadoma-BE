package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentvehicle.FinancierUseCase;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.Cooperation;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.financier.FinancierVehicleDetail;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentSummary;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.InviteFinancierRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.KycRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.FinancierRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.PaginatedResponse;
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

@Slf4j
@RestController
@RequiredArgsConstructor
public class FinancierController {
    private final FinancierUseCase financierUseCase;
    private final FinancierRestMapper financierRestMapper;
    private final InvestmentVehicleRestMapper investmentVehicleRestMapper;

    @PostMapping("financier/invite")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
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
        return financierRequests.stream().map(financierRequest ->{
            Financier financier = financierRestMapper.map(financierRequest);
            if (financierRequest.getFinancierType() == FinancierType.COOPERATE){
                mapCooperateValues(financierRequest, financier);
            } else if (financierRequest.getFinancierType() == FinancierType.INDIVIDUAL) {
                financier.setUserIdentity(financierRequest.getUserIdentity());
            }
            if (financier.getUserIdentity() == null){
                log.info("user identity is {}", financierRequest.getUserIdentity());
                financier.setUserIdentity(UserIdentity.builder().build());
            }

            financier.getUserIdentity().setCreatedBy(meedlUserId);
            financier.getUserIdentity().setCreatedAt(LocalDateTime.now());
            return financier;
        }).toList();
    }

    private static void mapCooperateValues(FinancierRequest financierRequest, Financier financier) {
        financier.setUserIdentity(UserIdentity.builder()
                .email(financierRequest.getOrganizationEmail())
                .createdAt(LocalDateTime.now())
                .role(IdentityRole.FINANCIER)
                .build());
        financier.setCooperation(Cooperation.builder()
                .name(financierRequest.getOrganizationName())
                .build());
    }

    @PostMapping("financier/complete-kyc")
    @PreAuthorize("hasRole('FINANCIER')")
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
    @PreAuthorize("hasRole('FINANCIER')")
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
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER') or hasRole('FINANCIER')")
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
    @GetMapping("financier/privacy-policy-decision")
    @PreAuthorize("hasRole('FINANCIER')")
    @FinancierInvestmentDetailDocs
    public ResponseEntity<ApiResponse<?>> makePrivacyPolicyDecision(@AuthenticationPrincipal Jwt meedlUser, @PathVariable boolean privacyPolicyDecision) throws MeedlException {
        Financier financier = financierRestMapper.map(meedlUser.getClaimAsString("sub"), privacyPolicyDecision);
        String response = financierUseCase.makePrivacyPolicyDecision(financier);

        ApiResponse<FinancierInvestmentDetailResponse> apiResponse = ApiResponse.<FinancierInvestmentDetailResponse>builder()
                .message(response)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("financier/view")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER') or hasRole('FINANCIER')")
    @FinancierDetail
    public ResponseEntity<ApiResponse<?>> viewFinancierDetail(@AuthenticationPrincipal Jwt meedlUser,@RequestParam(required = false) String financierId) throws MeedlException {
        String userId = meedlUser.getClaimAsString("sub");
        Financier financier = financierUseCase.viewFinancierDetail(userId, financierId);
        FinancierDashboardResponse financierDashboardResponse = financierRestMapper.mapToDashboardResponse(financier);

        ApiResponse<FinancierDashboardResponse> apiResponse = ApiResponse.<FinancierDashboardResponse>builder()
                .message(ControllerConstant.RETURNED_SUCCESSFULLY.getMessage())
                .data(financierDashboardResponse)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("financier/all/view")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public  ResponseEntity<ApiResponse<?>> viewAllFinancier(@AuthenticationPrincipal Jwt meedlUser,
                                                            @RequestParam int pageNumber,
                                                            @RequestParam int pageSize,
                                                            @RequestParam(required = false) String investmentVehicleId,
                                                            @RequestParam(required = false) FinancierType financierType,
                                                            @RequestParam(required = false) ActivationStatus activationStatus) throws MeedlException {
        Financier financier = Financier.builder().pageNumber(pageNumber).pageSize(pageSize).investmentVehicleId(investmentVehicleId).financierType(financierType).activationStatus(activationStatus).build();
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
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
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
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER') or hasRole('FINANCIER')")
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
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
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
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER') or hasRole('FINANCIER')")
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
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER') or hasRole('FINANCIER')")
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

}
