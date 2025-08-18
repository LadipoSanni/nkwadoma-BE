package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.investment;


import africa.nkwadoma.nkwadoma.application.ports.input.investmentvehicle.*;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleMode;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.PaginatedResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.invesmentvehicle.InvestmentVehicleRestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.constants.ControllerConstant;
import jakarta.validation.Valid;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.investmentVehicle.SuccessMessages.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class InvestmentVehicleController {

    private final InvestmentVehicleRestMapper investmentVehicleRestMapper;
    private final InvestmentVehicleUseCase investmentVehicleUseCase;

    @PostMapping("investment-vehicle")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
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

    @GetMapping("investment-vehicle-details/{investmentVehicleId}")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('FINANCIER')")
    public ResponseEntity<ApiResponse<?>> viewInvestmentVehicleDetails(@AuthenticationPrincipal Jwt meedlUser, @PathVariable String investmentVehicleId) throws MeedlException {
        String userId = meedlUser.getClaimAsString("sub");
        InvestmentVehicle investmentVehicle =
                investmentVehicleUseCase.viewInvestmentVehicleDetails(investmentVehicleId, userId);
        log.info("The investment vehicle found is {}", investmentVehicle);
        InvestmentVehicleResponse investmentVehicleResponse =
                investmentVehicleRestMapper.toInvestmentVehicleResponse(investmentVehicle);
        ApiResponse<InvestmentVehicleResponse> apiResponse = ApiResponse.<InvestmentVehicleResponse>builder()
                .data(investmentVehicleResponse)
                .message(INVESTMENT_VEHICLE_VIEWED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    @GetMapping("investment-vehicle/detail/link/{investmentVehicleLink}")
    public ResponseEntity<ApiResponse<?>> viewInvestmentVehicleDetailsViaLink(@PathVariable String investmentVehicleLink) throws MeedlException {

        InvestmentVehicle investmentVehicle =
                investmentVehicleUseCase.viewInvestmentVehicleDetailsViaLink(investmentVehicleLink);
        log.info("The investment vehicle found via link is {}", investmentVehicle);
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
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('FINANCIER')")
    public ResponseEntity<ApiResponse<?>> viewAllInvestmentVehicleDetails(
            @AuthenticationPrincipal Jwt meedlUser,
            @RequestParam int pageSize,
            @RequestParam int pageNumber) throws MeedlException {

        Page<InvestmentVehicle> investmentVehicles =
                investmentVehicleUseCase.viewAllInvestmentVehicle(meedlUser.getClaimAsString("sub"),pageSize, pageNumber);
        List<InvestmentVehicleResponse> investmentVehicleResponse =
                investmentVehicleRestMapper.toViewAllInvestmentVehicleResponse(investmentVehicles.getContent());

        ApiResponse<List<InvestmentVehicleResponse>> apiResponse = ApiResponse.<List<InvestmentVehicleResponse>>builder()
                .data(investmentVehicleResponse)
                .message(VIEW_ALL_INVESTMENT_VEHICLE)
                .statusCode(HttpStatus.OK.toString())
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @GetMapping("investment-vehicle/all/view/by")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('FINANCIER')")
    public ResponseEntity<ApiResponse<?>> viewAllInvestmentVehicleBy(
            @RequestParam int pageSize,
            @RequestParam int pageNumber,
            @RequestParam(required = false) InvestmentVehicleType investmentVehicleType,
            @RequestParam(required = false) InvestmentVehicleStatus investmentVehicleStatus,
            @RequestParam(required = false)InvestmentVehicleMode investmentVehicleMode,
            @RequestParam(required = false) String sortField,
            @AuthenticationPrincipal Jwt meedlUser) throws MeedlException {

        String userId = meedlUser.getClaimAsString("sub");
        InvestmentVehicle investmentVehicle = InvestmentVehicle.builder()
                .investmentVehicleType(investmentVehicleType)
                .investmentVehicleStatus(investmentVehicleStatus)
                .vehicleOperation(VehicleOperation.builder().fundRaisingStatus(investmentVehicleMode).build())
                .build();

        Page<InvestmentVehicle> investmentVehicles = investmentVehicleUseCase
                .viewAllInvestmentVehicleBy(pageSize, pageNumber, investmentVehicle, sortField, userId);
        List<InvestmentVehicleResponse> investmentVehicleResponse =
                investmentVehicles.stream().map(investmentVehicleRestMapper::toInvestmentVehicleResponse).toList();

        PaginatedResponse<InvestmentVehicleResponse> paginatedResponse = new PaginatedResponse<>(
                investmentVehicleResponse, investmentVehicles.hasNext(), investmentVehicles.getTotalPages(),investmentVehicles.getTotalElements(), pageNumber, pageSize);
        ApiResponse<PaginatedResponse<InvestmentVehicleResponse>> apiResponse = ApiResponse.<PaginatedResponse<InvestmentVehicleResponse>>builder()
                .data(paginatedResponse)
                .message(String.format("Investment vehicle %s", ControllerConstant.RETURNED_SUCCESSFULLY.getMessage()))
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("investmentvehicle/search/{investmentVehicleName}")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('FINANCIER')")
    public ResponseEntity<ApiResponse<?>> searchInvestmentVehicle(@AuthenticationPrincipal Jwt meedlUser,
                                                                  @PathVariable String investmentVehicleName,
                                                                  @RequestParam(required = false) InvestmentVehicleType investmentVehicleType,
                                                                  @RequestParam InvestmentVehicleStatus investmentVehicleStatus,
                                                                  @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                                  @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber
    ) throws MeedlException {
        InvestmentVehicle investmentVehicle = InvestmentVehicle.builder().investmentVehicleType(investmentVehicleType).
        name(investmentVehicleName).investmentVehicleStatus(investmentVehicleStatus).build();
        Page<InvestmentVehicle> investmentVehicles =
                investmentVehicleUseCase.searchInvestmentVehicle(meedlUser.getClaimAsString("sub"),investmentVehicle,
                        pageSize,pageNumber );
        List<InvestmentVehicleResponse> investmentVehicleResponses =
                investmentVehicles.stream().map(investmentVehicleRestMapper::toInvestmentVehicleResponse).toList();
        PaginatedResponse<InvestmentVehicleResponse> paginatedResponse = new PaginatedResponse<>(
                investmentVehicleResponses, investmentVehicles.hasNext(), investmentVehicles.getTotalPages(),investmentVehicles.getTotalElements(), pageNumber, pageSize);
        ApiResponse<PaginatedResponse<InvestmentVehicleResponse>> apiResponse = ApiResponse.<PaginatedResponse<InvestmentVehicleResponse>>builder()
                .data(paginatedResponse)
                .message(SEARCH_INVESTMENT_VEHICLE)
                .statusCode(HttpStatus.OK.toString())
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("investment-vehicle/visibility")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> setUpVisibility(@RequestBody InvestmentVehicleVisibilityRequest vehicleVisibilityRequest) throws MeedlException {
        InvestmentVehicle investmentVehicle = investmentVehicleUseCase.setInvestmentVehicleVisibility(vehicleVisibilityRequest.getInvestmentVehicleId(),
                vehicleVisibilityRequest.getVisibility(),vehicleVisibilityRequest.getFinanciers());
        InvestmentVehicleResponse investmentVehicleResponse =
                investmentVehicleRestMapper.toInvestmentVehicleResponse(investmentVehicle);
        ApiResponse<InvestmentVehicleResponse> apiResponse = ApiResponse.<InvestmentVehicleResponse>builder()
                .data(investmentVehicleResponse)
                .message(INVESTMENT_VEHICLE_VISIBILITY_UPDATED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("investment-vehicle/status")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> setUpInvestmentVehicleStatus(@RequestBody InvestmentVehicleOperationStatusRequest
                                                                                   vehicleOperationStatus) throws MeedlException{
        InvestmentVehicle investmentVehicle =
                investmentVehicleRestMapper.mapInvestmentVehicleOperationStatusToVehicleOperationStatus(vehicleOperationStatus);
        investmentVehicle =
                investmentVehicleUseCase.setInvestmentVehicleOperationStatus(investmentVehicle);
        InvestmentVehicleResponse investmentVehicleResponse =
                investmentVehicleRestMapper.toInvestmentVehicleResponse(investmentVehicle);
        ApiResponse<InvestmentVehicleResponse> apiResponse = ApiResponse.<InvestmentVehicleResponse>builder()
                .data(investmentVehicleResponse)
                .message(INVESTMENT_VEHICLE_STATUS_UPDATED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("delete-investment-vehicle/{investmentVehicleId}")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> deleteInvestmentVehicle(@PathVariable String investmentVehicleId) throws MeedlException {

        String response =
                investmentVehicleUseCase.deleteInvestmentVehicle(investmentVehicleId);
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .data(response)
                .message(INVESTMENT_VEHICLE_DELETED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("investmentVehicle/all/financier")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('FINANCIER') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> viewAllInvestmentVehicleInvestedInOrAddedTo(@AuthenticationPrincipal Jwt meedlUser,
                                                             @RequestParam(required = false) String financierId,
                                                             @RequestParam(required = false) InvestmentVehicleType investmentVehicleType,
                                                             @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                             @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {
        Page<InvestmentVehicle> investmentVehicles =
                investmentVehicleUseCase.viewAllInvestmentVehicleInvestedIn(meedlUser.getClaimAsString("sub"),financierId,
                        investmentVehicleType, pageSize,pageNumber );
        List<InvestmentVehicleResponse> investmentVehicleResponses =
                investmentVehicles.stream().map(investmentVehicleRestMapper::toInvestmentVehicleResponse).toList();
        PaginatedResponse<InvestmentVehicleResponse> paginatedResponse = new PaginatedResponse<>(
                investmentVehicleResponses, investmentVehicles.hasNext(), investmentVehicles.getTotalPages(),investmentVehicles.getTotalElements(), pageNumber, pageSize);
        ApiResponse<PaginatedResponse<InvestmentVehicleResponse>> apiResponse = ApiResponse.<PaginatedResponse<InvestmentVehicleResponse>>builder()
                .data(paginatedResponse)
                .message(VIEW_ALL_INVESTMENT_VEHICLE)
                .statusCode(HttpStatus.OK.toString())
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("investmentVehicle/search/financier/{investmentVehicleName}")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> searchMyInvestment(@AuthenticationPrincipal Jwt meedlUser,
                                                                  @PathVariable String investmentVehicleName,
                                                                  @RequestParam(required = false) InvestmentVehicleType investmentVehicleType,
                                                                  @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                                  @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {
        InvestmentVehicle investmentVehicle = InvestmentVehicle.builder().investmentVehicleType(investmentVehicleType).
                name(investmentVehicleName).build();
        Page<InvestmentVehicle> investmentVehicles =
                investmentVehicleUseCase.searchMyInvestment(meedlUser.getClaimAsString("sub"),investmentVehicle,
                        pageSize,pageNumber );
        List<InvestmentVehicleResponse> investmentVehicleResponses =
                investmentVehicles.stream().map(investmentVehicleRestMapper::toInvestmentVehicleResponse).toList();
        PaginatedResponse<InvestmentVehicleResponse> paginatedResponse = new PaginatedResponse<>(
                investmentVehicleResponses, investmentVehicles.hasNext(), investmentVehicles.getTotalPages(),investmentVehicles.getTotalElements(), pageNumber, pageSize);
        ApiResponse<PaginatedResponse<InvestmentVehicleResponse>> apiResponse = ApiResponse.<PaginatedResponse<InvestmentVehicleResponse>>builder()
                .data(paginatedResponse)
                .message(SEARCH_INVESTMENT_VEHICLE)
                .statusCode(HttpStatus.OK.toString())
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("fund-stack-holders")
    public ResponseEntity<ApiResponse<?>> viewFundShareHolders() throws MeedlException {
        FundStakeHolder fundStakeHolder =
                investmentVehicleUseCase.viewFundStakeHolders();
        ApiResponse<FundStakeHolder> apiResponse = ApiResponse.<FundStakeHolder>builder()
                .data(fundStakeHolder)
                .message("Fund stake holder returned successfully")
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
