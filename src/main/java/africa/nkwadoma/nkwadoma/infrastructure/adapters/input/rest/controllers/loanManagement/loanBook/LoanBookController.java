package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.application.ports.input.loanManagement.loanBook.LoanBookUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanBook;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.loanBook.LoanBookRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.LoanProductResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanBook.LoanBookResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement.loanBook.LoanBookRestMapper;
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
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.LOAN_BOOK_UPLOADED_SUCCESS;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.*;

@RequestMapping(BASE_URL + LOAN_BOOK)
@RequiredArgsConstructor
@RestController
@Slf4j
@Tag(name = LOAN_BOOK_CONTROLLER, description = LOAN_BOOK_CONTROLLER_DESCRIPTION)
public class LoanBookController {
    @Autowired
    private LoanBookRestMapper loanBookRestMapper;
    @Autowired
    private LoanBookUseCase loanBookUseCase;
    @PostMapping("/upload")
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    @Operation(summary = LOAN_PRODUCT_CREATION,description = LOAN_PRODUCT_CREATION_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> createLoanProduct (@AuthenticationPrincipal Jwt meedlUser, @RequestBody @Valid LoanBookRequest request) throws MeedlException {
        log.info("Upload loan book. Api .... ");
        LoanBook loanBook = loanBookRestMapper.map(request);
        loanBook.setCreatedBy(meedlUser.getClaimAsString("sub"));
        LoanBook loanBookReturned = loanBookUseCase.upLoadFile(loanBook);
        LoanBookResponse loanBookResponse = loanBookRestMapper.map(loanBookReturned);
        ApiResponse<LoanBookResponse> apiResponse = ApiResponse.<LoanBookResponse>builder()
                .data(loanBookResponse)
                .message(LOAN_BOOK_UPLOADED_SUCCESS)
                .statusCode(HttpStatus.CREATED.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
    }
}
