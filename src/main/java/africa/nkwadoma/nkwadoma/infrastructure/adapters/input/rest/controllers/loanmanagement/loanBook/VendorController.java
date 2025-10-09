package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.loanmanagement.loanBook;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanProduct.VendorUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Vendor;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.PaginatedResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanProduct.LoanProductResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanProduct.VendorResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement.loanProduct.VendorRestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.ControllerConstant;
import io.swagger.v3.oas.annotations.Operation;
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

import java.time.LocalDateTime;
import java.util.List;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.ControllerConstant.*;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.VENDOR;

@RequestMapping( VENDOR)
@RequiredArgsConstructor
@RestController
@Slf4j
public class VendorController {
    private final VendorUseCase vendorUseCase;
    private final VendorRestMapper vendorMapper;


    @GetMapping("/all")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
    @Operation(summary = VENDOR_VIEW_ALL, description = VENDOR_VIEW_ALL_DESCRIPTION )
    public ResponseEntity<ApiResponse<?>> viewAllLoanProduct(@AuthenticationPrincipal Jwt meedl,
                                                             @RequestParam(required = false, name = "name") String name,
                                                             @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                             @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {
        Vendor vendor = new Vendor();
        vendor.setVendorName(name);
        vendor.setPageSize(pageSize);
        vendor.setPageNumber(pageNumber);
        Page<Vendor> vendorPage = vendorUseCase.viewAllVendors(vendor);
        List<VendorResponse> vendorResponses = vendorPage.stream().map(vendorMapper::map).toList();
        PaginatedResponse<VendorResponse> paginatedResponse = new PaginatedResponse<>(
                vendorResponses, vendorPage.hasNext(), vendorPage.getTotalPages(),vendorPage.getTotalElements() , pageNumber,pageSize);
        log.info("View all vendor called successfully.");

        return new ResponseEntity<>(ApiResponse.builder().
                statusCode(HttpStatus.OK.toString()).
                data(paginatedResponse).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL).
                build(), HttpStatus.OK

        );
    }

    @GetMapping("/provider-service/all")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('MEEDL_ADMIN') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
    @Operation(summary = PROVIDER_SERVICE_VIEW_ALL, description = PROVIDER_SERVICE_VIEW_ALL_DESCRIPTION )
    public ResponseEntity<ApiResponse<?>> viewAllProviderService(@AuthenticationPrincipal Jwt meedl,
                                                                 @RequestParam(required = false, name = "name") String name,
                                                             @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                             @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {
        Vendor vendor = new Vendor();
        vendor.setVendorName(name);
        vendor.setPageSize(pageSize);
        vendor.setPageNumber(pageNumber);
        Page<String> providerServicePage = vendorUseCase.viewAllProviderService(vendor);
        List<String> providerServiceResponse = providerServicePage.stream().toList();
        PaginatedResponse<String> paginatedResponse = new PaginatedResponse<>(
                providerServiceResponse, providerServicePage.hasNext(), providerServicePage.getTotalPages(),providerServicePage.getTotalElements() , pageNumber,pageSize);
        log.info("View all provider service called successfully.");

        return new ResponseEntity<>(ApiResponse.builder()
                .statusCode(HttpStatus.OK.toString())
                .data(paginatedResponse)
                .message(ControllerConstant.RESPONSE_IS_SUCCESSFUL)
                .timeStamp(LocalDateTime.now())
                .build(), HttpStatus.OK
        );
    }
}
