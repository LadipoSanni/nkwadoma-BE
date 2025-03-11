package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;


import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.invesmentVehicle.InvestmentVehicleRestMapper;
import jakarta.validation.Valid;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.*;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.investmentVehicle.SuccessMessages.*;

@Slf4j
@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class InvestmentVehicleController {

    private final InvestmentVehicleRestMapper investmentVehicleRestMapper;
    private final InvestmentVehicleUseCase investmentVehicleUseCase;

    @PostMapping("investment-vehicle")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> setUpInvestmentVehicle(@Valid @RequestBody SetUpInvestmentVehicleRequest
                                                                          investmentVehicleRequest) throws MeedlException {
        InvestmentVehicle investmentVehicle =
                investmentVehicleRestMapper.toInvestmentVehicle(investmentVehicleRequest);
        investmentVehicle = investmentVehicleUseCase.setUpInvestmentVehicle(investmentVehicle);
        InvestmentVehicleResponse investmentVehicleResponse =
                investmentVehicleRestMapper.toInvestmentVehicleResponse(investmentVehicle);
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .data(investmentVehicleResponse)
                .message(INVESTMENT_VEHICLE_CREATED)
                .statusCode(HttpStatus.CREATED.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping("investment-vehicle-details/{id}")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> viewInvestmentVehicleDetails(@PathVariable String id) throws MeedlException {
        InvestmentVehicle investmentVehicle =
                investmentVehicleUseCase.viewInvestmentVehicleDetails(id);
        InvestmentVehicleResponse investmentVehicleResponse =
                investmentVehicleRestMapper.toInvestmentVehicleResponse(investmentVehicle);
        ApiResponse<InvestmentVehicleResponse> apiResponse = ApiResponse.<InvestmentVehicleResponse>builder()
                .data(investmentVehicleResponse)
                .message(INVESTMENT_VEHICLE_VIEWED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("view-all-investment-vehicle")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> viewAllInvestmentVehicleDetails(
            @RequestParam int pageSize,
            @RequestParam int pageNumber) {

        Page<InvestmentVehicle> investmentVehicles =
                investmentVehicleUseCase.viewAllInvestmentVehicle(pageSize, pageNumber);
        List<InvestmentVehicleResponse> investmentVehicleResponse =
                investmentVehicleRestMapper.toViewAllInvestmentVehicleResponse(investmentVehicles.getContent());

        ApiResponse<List<InvestmentVehicleResponse>> apiResponse = ApiResponse.<List<InvestmentVehicleResponse>>builder()
                .data(investmentVehicleResponse)
                .message(VIEW_ALL_INVESTMENT_VEHICLE)
                .statusCode(HttpStatus.OK.toString())
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("view-all-investment-vehicle-by-type")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> viewAllInvestmentVehicleType(
            @RequestParam int pageSize,
            @RequestParam int pageNumber,
            @RequestParam InvestmentVehicleType type) throws MeedlException {
        Page<InvestmentVehicle> investmentVehicles =
                investmentVehicleUseCase.viewAllInvestmentVehicleByType(pageSize, pageNumber, type);
        List<InvestmentVehicleResponse> investmentVehicleResponse =
                investmentVehicleRestMapper.toViewAllInvestmentVehicleResponse(investmentVehicles.getContent());

        ApiResponse<List<InvestmentVehicleResponse>> apiResponse = ApiResponse.<List<InvestmentVehicleResponse>>builder()
                .data(investmentVehicleResponse)
                .message(VIEW_ALL_INVESTMENT_VEHICLE)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("investment-vehicle/all/view")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> viewAllInvestmentVehicleFilter(
            @RequestParam(required = false) int pageSize,
            @RequestParam(required = false) int pageNumber,
            @RequestParam(required = false) InvestmentVehicleType investmentVehicleType,
            @RequestParam(required = false) InvestmentVehicleStatus investmentVehicleStatus,
            @RequestParam(required = false)FundRaisingStatus fundRaisingStatus) throws MeedlException {

        Page<InvestmentVehicle> investmentVehicles = investmentVehicleUseCase
                .viewAllInvestmentVehicleBy(pageSize, pageNumber, investmentVehicleType, investmentVehicleStatus, fundRaisingStatus);

        List<InvestmentVehicleResponse> investmentVehicleResponse =
                investmentVehicleRestMapper.toViewAllInvestmentVehicleResponse(investmentVehicles.getContent());

        ApiResponse<List<InvestmentVehicleResponse>> apiResponse = ApiResponse.<List<InvestmentVehicleResponse>>builder()
                .data(investmentVehicleResponse)
                .message(VIEW_ALL_INVESTMENT_VEHICLE)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("view-all-investment-vehicle-by-status")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> viewAllInvestmentVehicleType(
            @RequestParam int pageSize,
            @RequestParam int pageNumber,
            @RequestParam InvestmentVehicleStatus status) throws MeedlException {
        Page<InvestmentVehicle> investmentVehicles =
                investmentVehicleUseCase.viewAllInvestmentVehicleByStatus(pageSize, pageNumber, status);

        List<InvestmentVehicleResponse> investmentVehicleResponse =
                investmentVehicleRestMapper.toViewAllInvestmentVehicleResponse(investmentVehicles.getContent());

        ApiResponse<List<InvestmentVehicleResponse>> apiResponse = ApiResponse.<List<InvestmentVehicleResponse>>builder()
                .data(investmentVehicleResponse)
                .message(VIEW_ALL_INVESTMENT_VEHICLE)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("view-all-investment-vehicle-by-type-and-status")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> viewAllInvestmentVehicleTypeAndStatus(
            @RequestParam int pageSize,
            @RequestParam int pageNumber,
            @RequestParam InvestmentVehicleType type,
            @RequestParam InvestmentVehicleStatus status) throws MeedlException {
        Page<InvestmentVehicle> investmentVehicles =
                investmentVehicleUseCase.viewAllInvestmentVehicleByTypeAndStatus(pageSize, pageNumber, type, status);
        List<InvestmentVehicleResponse> investmentVehicleResponse =
                investmentVehicleRestMapper.toViewAllInvestmentVehicleResponse(investmentVehicles.getContent());

        ApiResponse<List<InvestmentVehicleResponse>> apiResponse = ApiResponse.<List<InvestmentVehicleResponse>>builder()
                .data(investmentVehicleResponse)
                .message(VIEW_ALL_INVESTMENT_VEHICLE)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("investmentvehicle/search/{investmentVehicleName}")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> searchInvestmentVehicle(@PathVariable String investmentVehicleName) throws MeedlException {

        List<InvestmentVehicle> investmentVehicles =
                investmentVehicleUseCase.searchInvestmentVehicle(investmentVehicleName);
        List<InvestmentVehicleResponse> investmentVehicleResponses =
                investmentVehicleRestMapper.toViewAllInvestmentVehicleResponse(investmentVehicles);

        ApiResponse<List<InvestmentVehicleResponse>> apiResponse = ApiResponse.<List<InvestmentVehicleResponse>>builder()
                .data(investmentVehicleResponses)
                .message(SEARCH_INVESTMENT_VEHICLE)
                .statusCode(HttpStatus.OK.toString())
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }



    @PostMapping("investment/publish/{investmentVehicleId}")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> publishInvestmentVehicle(@PathVariable String investmentVehicleId) throws MeedlException {
        InvestmentVehicle investmentVehicle =
                investmentVehicleUseCase.publishInvestmentVehicle(investmentVehicleId);
        InvestmentVehicleResponse investmentVehicleResponse =
                investmentVehicleRestMapper.toInvestmentVehicleResponse(investmentVehicle);
        ApiResponse<InvestmentVehicleResponse> apiResponse = ApiResponse.<InvestmentVehicleResponse>builder()
                .data(investmentVehicleResponse)
                .message(INVESTMENT_VEHICLE_PUBLISHED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
