package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.loanManagement.loanBook;


import africa.nkwadoma.nkwadoma.application.ports.input.loanManagement.loanBook.RepaymentHistoryUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.RepaymentHistoryPaginatedResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanBook.RepaymentHistoryResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanBook.YearRangeResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement.loanBook.RepaymentHistoryRestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.loan.SuccessMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.REPAYMENT_HISTORY;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.loan.SuccessMessages.YEAR_RANGE_RETRIEVED;


@Slf4j
@RequestMapping(BASE_URL +  REPAYMENT_HISTORY)
@RestController
@RequiredArgsConstructor
public class RepaymentHistoryController {


    private final RepaymentHistoryUseCase repaymentHistoryUseCase;
    private final RepaymentHistoryRestMapper repaymentHistoryRestMapper;

    @GetMapping("all")
    @PreAuthorize("hasRole('LOANEE') or hasRole('PORTFOLIO_MANAGER') ")
    public ResponseEntity<ApiResponse<?>> viewAllRepaymentHistory(@AuthenticationPrincipal Jwt meedlUser,
                                                                  @RequestParam(name = "loaneeId", required = false) String loaneeId,
                                                                  @RequestParam(name = "month", required = false) Integer month,
                                                                  @RequestParam(name = "year", required = false) Integer year,
                                                                  @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                                  @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {

        log.info("request that came in = pageSize = {}, pageNumber = {}, actor {}",pageSize,pageNumber,meedlUser.getClaimAsString("sub"));
        RepaymentHistory repaymentHistory =
                RepaymentHistory.builder().actorId(meedlUser.getClaimAsString("sub")).loaneeId(loaneeId)
                        .month(month).year(year).build();
        Page<RepaymentHistory>  repaymentHistories =
                repaymentHistoryUseCase.findAllRepaymentHistory(repaymentHistory,pageSize,pageNumber);
        log.info("repayment histories gotten from service {} , total element gotten  : {}",
                repaymentHistories.getContent().stream().toList(),repaymentHistories.getTotalElements());
        List<RepaymentHistoryResponse> repaymentHistoryResponse = repaymentHistories.stream()
                .map(repaymentHistoryRestMapper::toRepaymentResponse).toList();
        RepaymentHistoryPaginatedResponse<RepaymentHistoryResponse> paginatedResponse;
        if (repaymentHistoryResponse.isEmpty()) {
            paginatedResponse = new RepaymentHistoryPaginatedResponse<>(
                    repaymentHistoryResponse,repaymentHistories.hasNext(),repaymentHistories.getTotalPages(),pageNumber,pageSize,
                    0,0
            );
        }else {
            paginatedResponse = new RepaymentHistoryPaginatedResponse<>(
                    repaymentHistoryResponse, repaymentHistories.hasNext(), repaymentHistories.getTotalPages(), pageNumber, pageSize,
                    repaymentHistories.get().toList().get(0).getFirstYear(), repaymentHistories.get().toList().get(0).getLastYear()
            );
        }
        ApiResponse<RepaymentHistoryPaginatedResponse<RepaymentHistoryResponse>> apiResponse = ApiResponse.<RepaymentHistoryPaginatedResponse<RepaymentHistoryResponse>>builder()
                .data(paginatedResponse)
                .message(SuccessMessages.ALL_PAYMENT_HISTORY)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("search")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER') ")
    public ResponseEntity<ApiResponse<?>> searchRepaymentHistory(@AuthenticationPrincipal Jwt meedlUser,
                                                                  @RequestParam(name = "name", required = false) String name,
                                                                  @RequestParam(name = "month", required = false) Integer month,
                                                                  @RequestParam(name = "year", required = false) Integer year,
                                                                  @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                                  @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {

        RepaymentHistory repaymentHistory =
                RepaymentHistory.builder().actorId(meedlUser.getClaimAsString("sub")).loaneeName(name)
                        .month(month).year(year).build();
        Page<RepaymentHistory> repaymentHistories =
                repaymentHistoryUseCase.searchRepaymentHistory(repaymentHistory,pageSize,pageNumber);
        List<RepaymentHistoryResponse> repaymentHistoryResponse = repaymentHistories.stream()
                .map(repaymentHistoryRestMapper::toRepaymentResponse).toList();
        RepaymentHistoryPaginatedResponse<RepaymentHistoryResponse> paginatedResponse;
        if (repaymentHistoryResponse.isEmpty()) {
            paginatedResponse = new RepaymentHistoryPaginatedResponse<>(
                    repaymentHistoryResponse,repaymentHistories.hasNext(),repaymentHistories.getTotalPages(),pageNumber,pageSize,
                    0,0
            );
        }else {
            paginatedResponse = new RepaymentHistoryPaginatedResponse<>(
                    repaymentHistoryResponse, repaymentHistories.hasNext(), repaymentHistories.getTotalPages(), pageNumber, pageSize,
                    repaymentHistories.get().toList().get(0).getFirstYear(), repaymentHistories.get().toList().get(0).getLastYear()
            );
        }
        ApiResponse<RepaymentHistoryPaginatedResponse<RepaymentHistoryResponse>> apiResponse = ApiResponse.<RepaymentHistoryPaginatedResponse<RepaymentHistoryResponse>>builder()
                .data(paginatedResponse)
                .message(SuccessMessages.ALL_PAYMENT_HISTORY)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("years")
    @PreAuthorize("hasRole('LOANEE') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> getFirstAndLastYear(
            @AuthenticationPrincipal Jwt meedlUser,
            @RequestParam(name = "loaneeId", required = false) String loaneeId) throws MeedlException {
        log.info("Request to get first and last year for loaneeId : {}, actorId : {}", loaneeId, meedlUser.getClaimAsString("sub"));

        RepaymentHistory repaymentHistory = repaymentHistoryUseCase
                .getFirstRepaymentYearAndLastRepaymentYear(meedlUser.getClaimAsString("sub"), loaneeId);
        YearRangeResponse yearRangeResponse = repaymentHistoryRestMapper.toYearRange(repaymentHistory);

        ApiResponse<YearRangeResponse> apiResponse = ApiResponse.<YearRangeResponse>builder()
                .data(yearRangeResponse)
                .message(YEAR_RANGE_RETRIEVED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
