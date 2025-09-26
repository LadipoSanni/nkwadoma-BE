package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.meedlportfolio.PortfolioUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Demography;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.LoanProductResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.meedlPortfolio.DemographyResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.meedlPortfolio.PortfolioResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.meedlportfolio.PortfolioRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.ControllerConstant.*;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.MEEDL_OBLIGOR_LIMIT_SET_SUCCESSFULLY;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.MEEDL_OBLIGOR_LIMIT_VIEW_SUCCESSFULLY;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.meedlPortfolio.SuccessMessages.DEMOGRAPHY_VIEWED;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.meedlPortfolio.SuccessMessages.PORTFOLIO_VIEWED;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioUseCase portfolioUseCase;
    private final PortfolioRestMapper portfolioRestMapper;


    @GetMapping("meedl-portfolio/view")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
    public ResponseEntity<ApiResponse<?>> viewMeedlPortfolio() throws MeedlException {
        Portfolio portfolio =
                portfolioUseCase.viewPortfolio();
        PortfolioResponse portfolioResponse =
                portfolioRestMapper.toMeedlPortfolioResponse(portfolio);
        ApiResponse<PortfolioResponse> apiResponse = ApiResponse.<PortfolioResponse>builder()
                .data(portfolioResponse)
                .message(PORTFOLIO_VIEWED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("loanee-demography/view")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
    public ResponseEntity<ApiResponse<?>> viewLoaneeDemography() throws MeedlException {
        Demography demography =
                portfolioUseCase.viewLoaneeDemography();
        DemographyResponse demographyResponse =
                portfolioRestMapper.toDemographyResponse(demography);
        ApiResponse<DemographyResponse> apiResponse = ApiResponse.<DemographyResponse>builder()
                .data(demographyResponse)
                .message(DEMOGRAPHY_VIEWED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/meedl/obligor/limit/set-up")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN')")
    @Operation(summary = SET_MEEDL_OBLIGOR_LIMIT,description = SET_MEEDL_OBLIGOR_LIMIT_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> setUpMeedlObligorLoanLimit (
            @AuthenticationPrincipal Jwt meedlUser,
            @RequestParam
            BigDecimal obligorLoanLimit) throws MeedlException {
        log.info("set obligor loan limit for meedl actor id was called.... {}", meedlUser.getClaimAsString("sub"));
        Portfolio portfolio = Portfolio.builder().obligorLoanLimit(obligorLoanLimit).build();
        portfolioUseCase.setUpMeedlObligorLoanLimit(portfolio);
        ApiResponse<LoanProductResponse> apiResponse = ApiResponse.<LoanProductResponse>builder()
                .message(MEEDL_OBLIGOR_LIMIT_SET_SUCCESSFULLY)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }
    @GetMapping("/meedl/obligor/limit")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
    @Operation(summary = VIEW_MEEDL_OBLIGOR_LIMIT,description = VIEW_MEEDL_OBLIGOR_LIMIT_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> viewUpMeedlObligorLoanLimit (
            @AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        log.info("View obligor loan limit for meedl actor id was called.... {}", meedlUser.getClaimAsString("sub"));
        Portfolio portfolio = portfolioUseCase.viewMeedlObligorLoanLimit();
        ApiResponse<BigDecimal> apiResponse = ApiResponse.<BigDecimal>builder()
                .data(portfolio.getObligorLoanLimit())
                .message(MEEDL_OBLIGOR_LIMIT_VIEW_SUCCESSFULLY)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }
}
