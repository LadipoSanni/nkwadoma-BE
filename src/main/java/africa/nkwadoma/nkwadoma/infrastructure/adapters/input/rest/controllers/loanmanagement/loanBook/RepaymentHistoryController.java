package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.loanmanagement.loanBook;


import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook.RepaymentHistoryUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.PaginatedResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanBook.*;
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

import java.math.BigDecimal;
import java.util.List;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.REPAYMENT_HISTORY;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.loan.SuccessMessages.*;


@Slf4j
@RequestMapping(REPAYMENT_HISTORY)
@RestController
@RequiredArgsConstructor
public class RepaymentHistoryController {


    private final RepaymentHistoryUseCase repaymentHistoryUseCase;
    private final RepaymentHistoryRestMapper repaymentHistoryRestMapper;

    @GetMapping("all")
    @PreAuthorize("hasRole('LOANEE') or hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_ADMIN')  " +
            "or hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE') ")
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
        PaginatedResponse<RepaymentHistoryResponse> paginatedResponse = new PaginatedResponse<>(
                repaymentHistoryResponse,repaymentHistories.hasNext(),repaymentHistories.getTotalPages(),
                repaymentHistories.getTotalElements() ,pageNumber,pageSize
        );
        ApiResponse<PaginatedResponse<RepaymentHistoryResponse>> apiResponse = ApiResponse.<PaginatedResponse<RepaymentHistoryResponse>>builder()
                .data(paginatedResponse)
                .message(SuccessMessages.ALL_PAYMENT_HISTORY)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("search")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')  or hasRole('MEEDL_ADMIN')  or hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
    public ResponseEntity<ApiResponse<?>> searchRepaymentHistory(@AuthenticationPrincipal Jwt meedlUser,
                                                                  @RequestParam(name = "name", required = false) String name,
                                                                  @RequestParam(name = "month", required = false) Integer month,
                                                                  @RequestParam(name = "year", required = false) Integer year,
                                                                  @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                                  @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {

        if (name != null) name = name.trim();
        RepaymentHistory repaymentHistory =
                RepaymentHistory.builder().actorId(meedlUser.getClaimAsString("sub")).loaneeName(name)
                        .month(month).year(year).build();
        Page<RepaymentHistory> repaymentHistories =
                repaymentHistoryUseCase.searchRepaymentHistory(repaymentHistory,pageSize,pageNumber);
        List<RepaymentHistoryResponse> repaymentHistoryResponse = repaymentHistories.stream()
                .map(repaymentHistoryRestMapper::toRepaymentResponse).toList();
        PaginatedResponse<RepaymentHistoryResponse> paginatedResponse = new PaginatedResponse<>(
                repaymentHistoryResponse,repaymentHistories.hasNext(),repaymentHistories.getTotalPages(),
                repaymentHistories.getTotalElements() ,pageNumber,pageSize
        );
        ApiResponse<PaginatedResponse<RepaymentHistoryResponse>> apiResponse = ApiResponse.<PaginatedResponse<RepaymentHistoryResponse>>builder()
                .data(paginatedResponse)
                .message(SuccessMessages.ALL_PAYMENT_HISTORY)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("years")
    @PreAuthorize("hasRole('LOANEE') or hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_ADMIN')  or hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')" +
            "or hasRole('ORGANIZATION_SUPER_ADMIN') or hasRole('ORGANIZATION_ASSOCIATE') or hasRole('ORGANIZATION_ADMIN') ")
    public ResponseEntity<ApiResponse<?>> getFirstAndLastYear(
            @AuthenticationPrincipal Jwt meedlUser,
            @RequestParam(name = "loaneeId", required = false) String loaneeId,
            @RequestParam(name = "loanId", required = false) String loanId) throws MeedlException {
        log.info("Request to get first and last year for loaneeId : {}, actorId : {} , loanId : {}", loaneeId, meedlUser.getClaimAsString("sub"),loanId);

        RepaymentHistory repaymentHistory = repaymentHistoryUseCase
                .getFirstRepaymentYearAndLastRepaymentYear(meedlUser.getClaimAsString("sub"), loaneeId,loanId);
        YearRangeResponse yearRangeResponse = repaymentHistoryRestMapper.toYearRange(repaymentHistory);

        ApiResponse<YearRangeResponse> apiResponse = ApiResponse.<YearRangeResponse>builder()
                .data(yearRangeResponse)
                .message(YEAR_RANGE_RETRIEVED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @GetMapping("generate/repayment/schedule")
    @PreAuthorize("hasRole('LOANEE') or hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_ADMIN')  or hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')" +
            " or hasRole('ORGANIZATION_SUPER_ADMIN') or hasRole('ORGANIZATION_ASSOCIATE') or hasRole('ORGANIZATION_ADMIN') ")
    public ResponseEntity<ApiResponse<?>> generateRepaymentSchedule(@RequestParam(name = "amountApproved",required = false) BigDecimal amountApproved,
                                                                    @RequestParam(name = "loanProductId",required = false) String loanProductId,
                                                                    @RequestParam(name = "loanId",required = false)String loanId) throws MeedlException {

        log.info("request that got in amount approved is == {} , loan product id == {}, loan id is ==  {}",amountApproved,loanProductId,loanId);


        List<RepaymentHistory> repaymentHistory = repaymentHistoryUseCase.generateRepaymentHistory(amountApproved,loanProductId,loanId);

        List<RepaymentScheduleEntry> repaymentScheduleEntries = repaymentHistory.stream()
                .map(repaymentHistoryRestMapper::toRepaymentScheduleEntry).toList();

        RepaymentScheduleResponse repaymentScheduleResponse = new RepaymentScheduleResponse();
        repaymentScheduleResponse.setRepaymentScheduleEntries(repaymentScheduleEntries);
        repaymentScheduleResponse.setSumTotal(repaymentHistory.get(repaymentHistory.size() - 1).getTotalAmountRepaid());
        repaymentScheduleResponse.setTenor(repaymentHistory.get(repaymentHistory.size() - 1).getTenor());
        repaymentScheduleResponse.setMoratorium(repaymentHistory.get(repaymentHistory.size() - 1).getMoratorium());

        ApiResponse<RepaymentScheduleResponse> apiResponse = ApiResponse.<RepaymentScheduleResponse>builder()
                .data(repaymentScheduleResponse)
                .message(REPAYMENT_SCHEDULE)
                .statusCode(HttpStatus.OK.toString())
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @GetMapping("simulate")
    @PreAuthorize("hasRole('LOANEE')")
    public ResponseEntity<ApiResponse<?>> simulateRepayment(@RequestParam(name = "loanAmount")BigDecimal loanAmount,
                                                            @RequestParam(name = "interestRate")double interestRate,
                                                            @RequestParam(name = "repaymentPeriod")int repaymentPeriod) throws MeedlException {

        RepaymentHistory repaymentHistory = repaymentHistoryUseCase.simulateRepayment(loanAmount,interestRate,repaymentPeriod);
        SimulateRepaymentResponse simulateRepaymentResponse = repaymentHistoryRestMapper.toSimulateRepaymentResponse(repaymentHistory);
        ApiResponse<SimulateRepaymentResponse> apiResponse = ApiResponse.<SimulateRepaymentResponse>builder()
                .data(simulateRepaymentResponse)
                .message(REPAYMENT_SIMULATION)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @GetMapping("loan")
    @PreAuthorize("hasRole('LOANEE') or hasRole('MEEDL_SUPER_ADMIN') or hasRole('MEEDL_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')" +
            "or hasRole('ORGANIZATION_SUPER_ADMIN') or hasRole('ORGANIZATION_ASSOCIATE') or hasRole('ORGANIZATION_ADMIN') ")
    public ResponseEntity<ApiResponse<?>> viewAllRepaymentForALoan(@RequestParam(name = "loanId")String loanId,
                                                                   @RequestParam(name = "month", required = false) Integer month,
                                                                   @RequestParam(name = "year", required = false) Integer year,
                                                                   @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                                   @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {

        log.info("Request that got in ---- loan id {} ----- month {} ----- year {} --- pageSize {} ---- pageNumber {}",
                loanId,month,year,pageSize,pageNumber);

        RepaymentHistory repaymentHistory =
                RepaymentHistory.builder().loanId(loanId)
                        .month(month).year(year).build();
        Page<RepaymentHistory>  repaymentHistories = repaymentHistoryUseCase.findAllRepaymentHistoryByLoanId(repaymentHistory,pageSize,pageNumber);
        log.info("repayment histories gotten from service {} , total element gotten  : {}",
                repaymentHistories.getContent().stream().toList(),repaymentHistories.getTotalElements());
        List<RepaymentHistoryResponse> repaymentHistoryResponse = repaymentHistories.stream()
                .map(repaymentHistoryRestMapper::toRepaymentResponse).toList();
        PaginatedResponse<RepaymentHistoryResponse> paginatedResponse = new PaginatedResponse<>(
                repaymentHistoryResponse,repaymentHistories.hasNext(),repaymentHistories.getTotalPages(),
                repaymentHistories.getTotalElements() ,pageNumber,pageSize
        );
        ApiResponse<PaginatedResponse<RepaymentHistoryResponse>> apiResponse = ApiResponse.<PaginatedResponse<RepaymentHistoryResponse>>builder()
                .data(paginatedResponse)
                .message(SuccessMessages.PAYMENT_HISTORY_FOR_A_LOAN)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
