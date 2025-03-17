package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.FinancierUseCase;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.FinancierRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.PaginatedResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.FinancierResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.invesmentVehicle.FinancierRestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.enums.constants.ControllerConstant;
import jakarta.validation.Valid;
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

    @PostMapping("financier/invite")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public  ResponseEntity<ApiResponse<?>> inviteFinancierToVehicle(@AuthenticationPrincipal Jwt meedlUser, @RequestBody @Valid
    FinancierRequest financierRequest) throws MeedlException {
        Financier financier = financierRestMapper.map(financierRequest);
        financier.setIndividual(financierRequest.getIndividual());
        log.info("Mapped financier at controller {}", financier);
        financier.setInvitedBy(meedlUser.getClaimAsString("sub"));
        String message = financierUseCase.inviteFinancier(List.of(financier));

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message(message)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }
    @GetMapping("financier/view/{financierId}")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public  ResponseEntity<ApiResponse<?>> viewFinancierDetail(@AuthenticationPrincipal Jwt meedlUser,@PathVariable String financierId) throws MeedlException {
        Financier financier = financierUseCase.viewFinancierDetail(financierId);
        FinancierResponse financierResponse = financierRestMapper.map(financier);

        ApiResponse<FinancierResponse> apiResponse = ApiResponse.<FinancierResponse>builder()
                .data(financierResponse)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }
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
    public  ResponseEntity<ApiResponse<?>> search(@AuthenticationPrincipal Jwt meedlUser,@RequestParam String name) throws MeedlException {
        List<Financier> financiers = financierUseCase.search(name);
        List<FinancierResponse> financierResponses = financiers.stream().map(financierRestMapper::map).toList();
        log.info("Found financiers for search financier: {}", financiers);

        return new ResponseEntity<>(ApiResponse.builder().
                statusCode(HttpStatus.OK.toString()).
                data(financierResponses).
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
