package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.meedlportfolio.PortfolioUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.meedlPortfolio.PortfolioResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.meedlportfolio.PortfolioRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.meedlPortfolio.SuccessMessages.PORTFOLIO_VIEWED;

@RestController
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioUseCase portfolioUseCase;
    private final PortfolioRestMapper portfolioRestMapper;


    @GetMapping("meedl-portfolio/view")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
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
}
