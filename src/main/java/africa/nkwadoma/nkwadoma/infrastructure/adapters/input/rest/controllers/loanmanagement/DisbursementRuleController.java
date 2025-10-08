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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.ControllerConstant.*;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.*;
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

    @PostMapping("/update")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    @Operation(summary = UPDATE_DISBURSEMENT_RULE,description = UPDATE_DISBURSEMENT_RULE_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> updateDisbursementRule (
            @AuthenticationPrincipal Jwt meedlUser,
            @RequestBody DisbursementRuleRequest request) throws MeedlException {
        log.info("Update disbursement rule details called with id .... {}", request);
        DisbursementRule disbursementRule = disbursementRuleUseMapper.map(meedlUser.getClaim("sub"), request);
        DisbursementRule savedDisbursementRule = disbursementRuleUseCase.updateDisbursementRule(disbursementRule);
        DisbursementRuleResponse disbursementRuleResponse = disbursementRuleUseMapper.map(savedDisbursementRule);
        ApiResponse<DisbursementRuleResponse> apiResponse = ApiResponse.<DisbursementRuleResponse>builder()
                .data(disbursementRuleResponse)
                .message(DISBURSEMENT_RULE_UPDATE_SUCCESS)
                .statusCode(HttpStatus.CREATED.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
    }
    @GetMapping("/view/detail")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    @Operation(summary = VIEW_DISBURSEMENT_RULE,description = VIEW_DISBURSEMENT_RULE_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> viewDisbursementRuleDetail (
            @AuthenticationPrincipal Jwt meedlUser,
            @RequestParam String id) throws MeedlException {
        log.info("View disbursement rule details called with id .... {}", id);
        DisbursementRule disbursementRule = disbursementRuleUseMapper.map(meedlUser.getClaim("sub"), id);
        DisbursementRule savedDisbursementRule = disbursementRuleUseCase.viewDisbursementRule(disbursementRule);
        DisbursementRuleResponse disbursementRuleResponse = disbursementRuleUseMapper.map(savedDisbursementRule);
        ApiResponse<DisbursementRuleResponse> apiResponse = ApiResponse.<DisbursementRuleResponse>builder()
                .data(disbursementRuleResponse)
                .message(DISBURSEMENT_RULE_VIEW_DETAIL_SUCCESS)
                .statusCode(HttpStatus.CREATED.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.FOUND);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER') or hasRole('PORTFOLIO_MANAGER_ASSOCIATE')")
    public ResponseEntity<ApiResponse<?>> searchDisbursementRule (
            @Valid @RequestParam(name = "name") @NotBlank(message = "Disbursement rule name is required") String name,
            @AuthenticationPrincipal Jwt meedlUser,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber
    ) throws MeedlException {
        DisbursementRule disbursementRule = DisbursementRule.builder()
                .name(name)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();
        Page<DisbursementRule> disbursementRulePage = disbursementRuleUseCase.search(disbursementRule);
        DisbursementRuleResponse disbursementRuleResponse = disbursementRuleUseMapper.map(disbursementRule);
        ApiResponse<DisbursementRuleResponse> apiResponse = ApiResponse.<DisbursementRuleResponse>builder()
                .data(disbursementRuleResponse)
                .message(DISBURSEMENT_RULE_VIEW_DETAIL_SUCCESS)
                .statusCode(HttpStatus.CREATED.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.FOUND);
    }


    @PutMapping("/respond")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN')")
    @Operation(summary = RESPOND_TO_DISBURSEMENT_RULE,description = RESPOND_TO_DISBURSEMENT_RULE_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> respondToDisbursementRule (
            @AuthenticationPrincipal Jwt meedlUser,
            @RequestBody DisbursementRuleRequest disbursementRuleRequest) throws MeedlException {
        log.info("Respond to disbursement rule called with id .... {}", disbursementRuleRequest.getId());
        DisbursementRule disbursementRule = disbursementRuleUseMapper.map(meedlUser.getClaim("sub"), disbursementRuleRequest);
        DisbursementRule savedDisbursementRule = disbursementRuleUseCase.respondToDisbursementRule(disbursementRule);
        DisbursementRuleResponse disbursementRuleResponse = disbursementRuleUseMapper.map(savedDisbursementRule);
        ApiResponse<DisbursementRuleResponse> apiResponse = ApiResponse.<DisbursementRuleResponse>builder()
                .data(disbursementRuleResponse)
                .message(DISBURSEMENT_RULE_VIEW_DETAIL_SUCCESS)
                .statusCode(HttpStatus.CREATED.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }
}
