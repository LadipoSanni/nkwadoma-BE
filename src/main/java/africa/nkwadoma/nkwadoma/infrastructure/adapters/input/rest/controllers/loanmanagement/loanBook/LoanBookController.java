package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.loanmanagement.loanBook;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook.LoanBookUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement.loanBook.LoanBookRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.LOAN_BOOK_UPLOADED_PROCESSING;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.REPAYMENT_RECORD_BOOK_UPLOADED_SUCCESS;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.*;

@RequestMapping( LOAN_BOOK)
@RequiredArgsConstructor
@RestController
@Slf4j
@Tag(name = LOAN_BOOK_CONTROLLER, description = LOAN_BOOK_CONTROLLER_DESCRIPTION)
public class LoanBookController {

    @Autowired
    private LoanBookRestMapper loanBookRestMapper;
    @Autowired
    private LoanBookUseCase loanBookUseCase;

    @PostMapping(value = "/upload/{cohortId}/file/loanee/data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    @Operation(summary = LOAN_BOOK_USER_DATA_CREATION_VIA_FILE_UPLOAD,description = LOAN_BOOK_USER_DATA_CREATION_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> uploadLoanBookUserData(@AuthenticationPrincipal Jwt meedlUser,
                                                         @RequestPart("file") MultipartFile file,
                                                         @PathVariable String cohortId
                                                            ) throws MeedlException {
        log.info("Upload loan book. Api called .... ");

        LoanBook loanBook = mapLoanBookRequest(meedlUser, file, cohortId);
        loanBookUseCase.upLoadUserData(loanBook);
        log.info("The loan book returned after processing {}", loanBook);
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message(LOAN_BOOK_UPLOADED_PROCESSING)
                .statusCode(HttpStatus.CREATED.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
    }

    @PostMapping(value = "/upload/{cohortId}/file/loanee/repayment/record", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    @Operation(summary = LOAN_BOOK_REPAYMENT_RECORD_CREATION_VIA_FILE_UPLOAD,description = LOAN_BOOK_REPAYMENT_RECORD_CREATION_DESCRIPTION)
    public ResponseEntity<ApiResponse<?>> uploadLoanBookRepaymentRecord(@AuthenticationPrincipal Jwt meedlUser,
                                                         @RequestPart("file") MultipartFile file,
                                                         @PathVariable String cohortId
                                                        ) throws MeedlException {
        log.info("Repayment record book. Api called .... ");
        LoanBook loanBook = mapLoanBookRequest(meedlUser, file, cohortId);
        loanBookUseCase.uploadRepaymentHistory(loanBook);
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message(REPAYMENT_RECORD_BOOK_UPLOADED_SUCCESS)
                .statusCode(HttpStatus.CREATED.toString())
                .build();
        return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
    }
    private LoanBook mapLoanBookRequest(Jwt meedlUser, MultipartFile file, String cohortId) throws MeedlException {
        LoanBook loanBook = new LoanBook();
        loanBook.setFile(convertToTempFile(file));
        loanBook.setActorId(meedlUser.getClaimAsString("sub"));
        loanBook.setCohort(Cohort.builder().id(cohortId).createdBy(meedlUser.getClaimAsString("sub")).build());
        return loanBook;
    }
    private File convertToTempFile(MultipartFile multipartFile) throws MeedlException {
        String originalFilename = multipartFile.getOriginalFilename();

        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new MeedlException("File must have a valid extension");
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
