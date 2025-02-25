package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.meedlPortfolio.MeedlPortfolioUseCase;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.MeedlPortfolio;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education.CohortResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.meedlPortfolio.MeedlPortfolioResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.meedlPortfolio.MeedlPortfolioRestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.MeedlPortfolioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.cohort.SuccessMessages.COHORT_CREATED;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.meedlPortfolio.SuccessMessages.PORTFOLIO_VIEWED;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class MeedlPortfolioController {

    private final MeedlPortfolioUseCase meedlPortfolioUseCase;
    private final MeedlPortfolioRestMapper meedlPortfolioRestMapper;


    @GetMapping("meedl-portfolio/view")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> viewMeedlPortfolio() {
        MeedlPortfolio meedlPortfolio =
                meedlPortfolioUseCase.viewMeedlPortfolio();
        MeedlPortfolioResponse meedlPortfolioResponse =
                meedlPortfolioRestMapper.toMeedlPortfolioResponse(meedlPortfolio);
        ApiResponse<MeedlPortfolioResponse> apiResponse = ApiResponse.<MeedlPortfolioResponse>builder()
                .data(meedlPortfolioResponse)
                .message(PORTFOLIO_VIEWED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
