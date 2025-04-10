package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.FinancierUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Cooperation;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.FinancierVehicleDetail;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.InviteFinancierRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.KycRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.FinancierRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.PaginatedResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.FinancierDashboardResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.FinancierInvestmentDetailResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.KycResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.FinancierResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.invesmentVehicle.FinancierRestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.swagger.SwaggerDocumentation;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.invesmentVehicle.InvestmentVehicleFinancierRestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.investmentVehicle.InvestmentVehicleFinancierMapper;
import africa.nkwadoma.nkwadoma.infrastructure.enums.constants.ControllerConstant;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;

@Slf4j
@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class FinancierController {
    private final FinancierUseCase financierUseCase;
    private final FinancierRestMapper financierRestMapper;
    private final InvestmentVehicleFinancierMapper investmentVehicleFinancierMapper;
    private final InvestmentVehicleFinancierRestMapper investmentVehicleFinancierRestMapper;
    private final FinancierOutputPort financierOutputPort;

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
                financier.getUserIdentity().setCreatedBy(meedlUserId);
            } else if (financierRequest.getFinancierType() == FinancierType.INDIVIDUAL) {
                financier.setUserIdentity(financierRequest.getUserIdentity());
                financier.getUserIdentity().setCreatedBy(meedlUserId);
            }
            return financier;
        }).toList();
    }

    private static void mapCooperateValues(FinancierRequest financierRequest, Financier financier) {
        financier.setUserIdentity(UserIdentity.builder()
                .email(financierRequest.getOrganizationEmail())
                .firstName("cooperateFinancier")
                .lastName("admin")
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

    @GetMapping("financier/view/investment-details/{financierId}")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    @Operation(
            summary = SwaggerDocumentation.VIEW_INVESTMENT_DETAILS_OF_FINANCIER,
            description = SwaggerDocumentation.VIEW_INVESTMENT_DETAILS_OF_FINANCIER_DESCRIPTION,
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved financier investment details",
                            content = @Content(schema = @Schema(implementation = InvestmentVehicleMessages.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid financier id provided.",
                            content = @Content(schema = @Schema(implementation = InvestmentVehicleMessages.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Financier not found"
                    )
            }
    )
    public ResponseEntity<ApiResponse<?>> viewInvestmentDetailsOfFinancier(@PathVariable String financierId) throws MeedlException {
        FinancierVehicleDetail financierVehicleDetail = financierUseCase.viewInvestmentDetailsOfFinancier(financierId);
        FinancierInvestmentDetailResponse financierInvestmentDetailResponse = financierRestMapper.mapToFinancierDetailResponse(financierVehicleDetail);

        ApiResponse<FinancierInvestmentDetailResponse> apiResponse = ApiResponse.<FinancierInvestmentDetailResponse>builder()
                .data(financierInvestmentDetailResponse)
                .message(ControllerConstant.VIEW_EMPLOYEE_DETAILS_SUCCESSFULLY.getMessage())
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("financier/view/investment-details")
    @PreAuthorize("hasRole('FINANCIER')")
    @Operation(
            summary = SwaggerDocumentation.VIEW_INVESTMENT_DETAILS,
            description = SwaggerDocumentation.VIEW_INVESTMENT_DETAILS_DESCRIPTION,
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved financier investment details",
                            content = @Content(schema = @Schema(implementation = InvestmentVehicleMessages.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid financier id provided.",
                            content = @Content(schema = @Schema(implementation = InvestmentVehicleMessages.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Financier not found"
                    )
            }
    )
    public ResponseEntity<ApiResponse<?>> viewInvestmentDetails(@AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        Financier financier = financierOutputPort.findFinancierByUserId(meedlUser.getClaimAsString("sub"));
        FinancierVehicleDetail financierVehicleDetail = financierUseCase.viewInvestmentDetailsOfFinancier(financier.getId());
        FinancierInvestmentDetailResponse financierInvestmentDetailResponse = financierRestMapper.mapToFinancierDetailResponse(financierVehicleDetail);

        ApiResponse<FinancierInvestmentDetailResponse> apiResponse = ApiResponse.<FinancierInvestmentDetailResponse>builder()
                .data(financierInvestmentDetailResponse)
                .message(ControllerConstant.VIEW_EMPLOYEE_DETAILS_SUCCESSFULLY.getMessage())
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("financier/view/{financierId}")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER') or hasRole('FINANCIER')")
    public ResponseEntity<ApiResponse<?>> viewFinancierDetail(@AuthenticationPrincipal Jwt meedlUser,@PathVariable(required = false) String financierId) throws MeedlException {
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

//    @GetMapping("financier/view/dashboard")
//    @PreAuthorize("hasRole('FINANCIER')")
//    public ResponseEntity<ApiResponse<?>> viewMyDashboard(@AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
//        Financier foundFinancier = financierUseCase.findFinancierByUserId(meedlUser.getClaimAsString("sub"));
//        FinancierDashboardResponse financierDashboardResponse = financierRestMapper.mapToDashboardResponse(foundFinancier);
//
//        ApiResponse<FinancierDashboardResponse> apiResponse = ApiResponse.<FinancierDashboardResponse>builder()
//                .message(ControllerConstant.RETURNED_SUCCESSFULLY.getMessage())
//                .data(financierDashboardResponse)
//                .statusCode(HttpStatus.OK.toString())
//                .build();
//        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
//    }

    @GetMapping("financier/all/view")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public  ResponseEntity<ApiResponse<?>> viewAllFinancier(@AuthenticationPrincipal Jwt meedlUser,@RequestParam int pageNumber,@RequestParam int pageSize) throws MeedlException {
       Financier financier = Financier.builder().pageNumber(pageNumber).pageSize(pageSize).build();
        Page<Financier> financiers = financierUseCase.viewAllFinancier(financier);
        List<FinancierResponse > financierResponses = financiers.stream().map(financierRestMapper::map).toList();
        log.info("financiers mapped for view all financiers on the platform: {}", financierResponses);
        PaginatedResponse<FinancierResponse> response = new PaginatedResponse<>(
                financierResponses, financiers.hasNext(),
                financiers.getTotalPages(), pageNumber, pageSize
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
                                                  @RequestParam(required = false) ActivationStatus activationStatus) throws MeedlException {
        Page<Financier> financiers = financierUseCase.search(name, pageNumber, pageSize);
        List<FinancierResponse> financierResponses = financiers.stream().map(financierRestMapper::map).toList();
        log.info("Found financiers for search financier: {}", financiers);
        PaginatedResponse<FinancierResponse> response = new PaginatedResponse<>(
                financierResponses, financiers.hasNext(),
                financiers.getTotalPages(), pageNumber, pageSize
        );

        return new ResponseEntity<>(ApiResponse.builder().
                statusCode(HttpStatus.OK.toString()).
                data(response).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                build(), HttpStatus.OK
        );
    }
    @GetMapping("financier/investment-vehicle/all/view")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public  ResponseEntity<ApiResponse<?>> viewAllFinancierInInvestmentVehicle(@AuthenticationPrincipal Jwt meedlUser,
                                                            @RequestParam int pageNumber,
                                                            @RequestParam int pageSize,
                                                            @RequestParam(required = false) ActivationStatus activationStatus,
                                                            @RequestParam String investmentVehicleId) throws MeedlException {
        Financier financier = Financier.builder().investmentVehicleId(investmentVehicleId).pageNumber(pageNumber).pageSize(pageSize).build();
        Page<Financier> financiers = viewAllBasedOnActivationStatus(activationStatus, financier);
        List<FinancierResponse> financierResponses = financiers.stream().map(financierRestMapper::map).toList();
        log.info("View all financier in investment vehicle. Financiers mapped: {} in ", financierResponses);
        PaginatedResponse<FinancierResponse> response = new PaginatedResponse<>(
                financierResponses, financiers.hasNext(),
                financiers.getTotalPages(), pageNumber, pageSize
        );
        return new ResponseEntity<>(ApiResponse.builder().
                statusCode(HttpStatus.OK.toString()).
                data(response).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                build(), HttpStatus.OK
        );
    }

    private Page<Financier> viewAllBasedOnActivationStatus(ActivationStatus activationStatus, Financier financier) throws MeedlException {
        Page<Financier> financiers;
        if (activationStatus != null) {
            financier.setActivationStatus(activationStatus);
            financiers = financierUseCase.viewAllFinancierInInvestmentVehicleByActivationStatus(financier);
        } else {
            financiers = financierUseCase.viewAllFinancierInInvestmentVehicle(financier);
        }
        return financiers;
    }


}
