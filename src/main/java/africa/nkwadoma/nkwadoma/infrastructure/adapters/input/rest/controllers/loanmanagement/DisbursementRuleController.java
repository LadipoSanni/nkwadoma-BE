package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.DisbursementRuleUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.DisbursementRule;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.DisbursementRuleRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoanProductRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.QAResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.DisbursementRuleResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.LoanProductResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement.DisbursementRuleRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.DISBURSEMENT_RULE_CREATED_SUCCESS;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.UPDATED_LOAN_PRODUCT_SUCCESS;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.DISBURSEMENT_RULE;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.LOAN;

@RequestMapping( DISBURSEMENT_RULE)
@RequiredArgsConstructor
@RestController
@Slf4j
public class DisbursementRuleController {

    private final DisbursementRuleUseCase disbursementRuleUseCase;
    private final DisbursementRuleRestMapper disbursementRuleUseMapper;

    @PostMapping("/create")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    @Operation(summary = CREATE_DISBURSEMENT_RULE,description = CREATE_DISBURSEMENT_RULE_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> createDisbursementRule (
            @AuthenticationPrincipal Jwt meedlUser,
            @RequestBody DisbursementRuleRequest request) throws MeedlException {
        log.info("Create disbursement rule called with id .... {}", request.getName());
        DisbursementRule disbursementRule = disbursementRuleUseMapper.map(meedlUser.getClaim("sub"), request);
        DisbursementRule savedDisbursementRule = disbursementRuleUseCase.createDisbursementRule(disbursementRule);
        DisbursementRuleResponse disbursementRuleResponse = disbursementRuleUseMapper.map(savedDisbursementRule);
        ApiResponse<QAResponse> apiResponse = ApiResponse.<QAResponse>builder()
                .data(QAResponse.builder().id(disbursementRuleResponse.getId()).build())
                .message(DISBURSEMENT_RULE_CREATED_SUCCESS)
                .statusCode(HttpStatus.CREATED.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
    }

}
