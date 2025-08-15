package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.walletManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.walletManagement.BankDetailUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.OrganizationRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.walletManagement.BankDetailRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.QAResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.walletManagement.BankDetailRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.ControllerConstant.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("program")
@Tag(name = "Program Controller", description = "Manage Programs in an institute")
public class WalletManagementController {
    @Autowired
    private BankDetailUseCase bankDetailUseCase;
    @Autowired
    private BankDetailRestMapper bankDetailMapper;

    @PostMapping("add/bankDetail")
    @Operation(summary = BANK_DETAIL, description = INVITE_ORGANIZATION_DESCRIPTION)
    @PreAuthorize(""" 
            hasRole('MEEDL_SUPER_ADMIN')
            or hasRole('MEEDL_ADMIN')
            or hasRole('MEEDL_ASSOCIATE')
            or hasRole('PORTFOLIO_MANAGER')
            or hasRole('ORGANIZATION_SUPER_ADMIN')
            or hasRole('ORGANIZATION_ADMIN')
            or hasRole('ORGANIZATION_ASSOCIATE')
            """)
            public ResponseEntity<ApiResponse<?>> addBankDetail(@AuthenticationPrincipal Jwt meedlUser,
                                                             @RequestBody @Valid BankDetailRequest bankDetailRequest) throws MeedlException {
        BankDetail bankDetail = bankDetailMapper.map(meedlUser.getClaimAsString("sub"), bankDetailRequest);
        bankDetail = bankDetailUseCase.addBankDetails(bankDetail);
        log.info("Bank details after adding {}", bankDetail);
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .data(QAResponse.build(bankDetail.getId()))
                .message(bankDetail.getResponse())
                .statusCode(HttpStatus.CREATED.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

}
