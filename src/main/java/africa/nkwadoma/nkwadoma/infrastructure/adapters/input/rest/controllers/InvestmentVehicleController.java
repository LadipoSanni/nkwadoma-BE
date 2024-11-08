package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;


import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.*;
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
    private final CreateInvestmentVehicleUseCase investmentVehicleUseCase;

    @PostMapping("investment-vehicle")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> createInvestmentVehicle(@RequestBody CreateInvestmentVehicleRequest
                                                                          investmentVehicleRequest) throws MeedlException {
        InvestmentVehicle investmentVehicle =
                investmentVehicleRestMapper.toInvestmentVehicle(investmentVehicleRequest);
        investmentVehicle = investmentVehicleUseCase.createOrUpdateInvestmentVehicle(investmentVehicle);
        InvestmentVehicleResponse investmentVehicleResponse =
                investmentVehicleRestMapper.toInvestmentVehicleResponse(investmentVehicle);
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .data(investmentVehicleResponse)
                .message(INVESTMENT_VEHICLE_CREATED)
                .status(HttpStatus.CREATED.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PostMapping("update-investment-vehicle")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> updateInvestmentVehicle(@RequestBody UpdateInvestmentVehicleRequest
                                                                          investmentVehicleRequest) throws MeedlException {
        InvestmentVehicle investmentVehicle =
                investmentVehicleRestMapper.mapUpdateInvestmentVehicleRequestToInvestmentVehicle(investmentVehicleRequest);
        investmentVehicle = investmentVehicleUseCase.createOrUpdateInvestmentVehicle(investmentVehicle);
        InvestmentVehicleResponse updateInvestmentVehicleResponse =
                investmentVehicleRestMapper.toInvestmentVehicleResponse(investmentVehicle);
        ApiResponse<InvestmentVehicleResponse> apiResponse = ApiResponse.<InvestmentVehicleResponse>builder()
                .data(updateInvestmentVehicleResponse)
                .message(INVESTMENT_VEHICLE_UPDATED)
                .status(HttpStatus.OK.toString())
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
                .status(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.FOUND);
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
                .status(HttpStatus.OK.toString())
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
