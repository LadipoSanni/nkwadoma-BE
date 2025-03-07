package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.FinancierUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.FinancierRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.FinancierResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.invesmentVehicle.FinancierRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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
        String message = financierUseCase.inviteFinancier(financier);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message(message)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }
    @GetMapping("financier/view/{financierId}")
    public  ResponseEntity<ApiResponse<?>> inviteFinancierToVehicle(@AuthenticationPrincipal Jwt meedlUser,@PathVariable String financierId) throws MeedlException {
        Financier financier = financierUseCase.viewFinancierDetail(financierId);
        FinancierResponse financierResponse = financierRestMapper.map(financier);

        ApiResponse<FinancierResponse> apiResponse = ApiResponse.<FinancierResponse>builder()
                .data(financierResponse)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

}
