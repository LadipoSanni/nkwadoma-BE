package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.application.ports.input.loanManagement.loanBook.LoanBookUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanBook;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.CreateCohortRequest;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

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
    @PostMapping(value = "/upload/{cohortId}/{loanProductId}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('PORTFOLIO_MANAGER')")
    @Operation(summary = LOAN_BOOK_CREATION_VIA_FILE_UPLOAD,description = LOAN_BOOK_CREATION_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> createLoanBook (@AuthenticationPrincipal Jwt meedlUser,
                                                             @RequestPart("file") MultipartFile file,
                                                             @PathVariable String cohortId,
                                                             @PathVariable String loanProductId
                                                            ) throws MeedlException {
        log.info("Upload loan book. Api .... ");
//        LoanBook loanBook = loanBookRestMapper.map(cohortId, convertToTempFile(file), meedlUser.getClaimAsString("sub") );
//        LoanBook loanBook = loanBookRestMapper.map(convertToTempFile(file));
        LoanBook loanBook = new LoanBook();
        loanBook.setFile(convertToTempFile(file));
        loanBook.setCohort(Cohort.builder().id(cohortId).createdBy( meedlUser.getClaimAsString("sub")).build());
        LoanBook loanBookReturned = loanBookUseCase.upLoadFile(loanBook);
        LoanBookResponse loanBookResponse = new LoanBookResponse();
        loanBookResponse.setCohort(loanBookReturned.getCohort());
        loanBookResponse.setLoanees(loanBookReturned.getLoanees());
        ApiResponse<LoanBookResponse> apiResponse = ApiResponse.<LoanBookResponse>builder()
                .data(loanBookResponse)
                .message(LOAN_BOOK_UPLOADED_SUCCESS)
                .statusCode(HttpStatus.CREATED.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
    }
    private File convertToTempFile(MultipartFile multipartFile) throws MeedlException {
        String originalFilename = multipartFile.getOriginalFilename();

        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("File must have a valid extension");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        File tempFile;
        try {

            tempFile = File.createTempFile("upload-", extension);
            multipartFile.transferTo(tempFile);
        } catch (IOException e) {
            log.error("Error at the loan book upload controller. File format doesn't match.", e);
            throw new MeedlException("Error converting accessing file format.");

        }
        return tempFile;
    }
}
