package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;


import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.CreateInvestmentVehicleUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.CreateInvestmentVehicleRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.UpdateInvestmentVehicleRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.CreateInvestmentVehicleResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.UpdateInvestmentVehicleResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.InvestmentVehicleRestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.ErrorMessages.INVALID_OPERATION;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;
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
                                                                              investmentVehicleRequest){
        try {
            InvestmentVehicle investmentVehicle =
                    investmentVehicleRestMapper.toInvestmentVehicle(investmentVehicleRequest);
            investmentVehicle = investmentVehicleUseCase.createOrUpdateInvestmentVehicle(investmentVehicle);
            CreateInvestmentVehicleResponse investmentVehicleResponse  =
                    investmentVehicleRestMapper.toCreateInvestmentVehicleResponse(investmentVehicle);
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .body(investmentVehicleResponse)
                    .message(INVESTMENT_VEHICLE_CREATED)
                    .statusCode(HttpStatus.CREATED.toString())
                    .build();
            return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
        } catch (MeedlException e) {
            return new ResponseEntity<>(new ApiResponse<>(INVALID_OPERATION, e.getMessage(),
                    HttpStatus.BAD_REQUEST.toString()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("update-investment-vehicle")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> updateInvestmentVehicle(@RequestBody UpdateInvestmentVehicleRequest
                                                                          investmentVehicleRequest){
        try {
            InvestmentVehicle investmentVehicle =
                    investmentVehicleRestMapper.mapUpdateInvestmentVehicleRequestToInvestmentVehicle(investmentVehicleRequest);
            investmentVehicle = investmentVehicleUseCase.createOrUpdateInvestmentVehicle(investmentVehicle);
            UpdateInvestmentVehicleResponse updateInvestmentVehicleResponse =
                    investmentVehicleRestMapper.toUpdateInvestmentVehicleResponse(investmentVehicle);
            ApiResponse<Object> apiResponse =ApiResponse.builder()
                    .body(updateInvestmentVehicleResponse)
                    .message(INVESTMENT_VEHICLE_UPDATED)
                    .statusCode(HttpStatus.OK.toString())
                    .build();
            return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
        } catch (MeedlException e) {
            return new ResponseEntity<>(new ApiResponse<>(INVALID_OPERATION, e.getMessage(),
                    HttpStatus.BAD_REQUEST.toString()), HttpStatus.BAD_REQUEST);
        }
    }
}
