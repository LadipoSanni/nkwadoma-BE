package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;


import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.CreateCohortRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.EditCohortLoanDetailRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education.CohortResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.education.CohortRestMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.ErrorMessages.INVALID_OPERATION;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.cohort.SuccessMessages.*;

@Slf4j
@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class CohortController {


    private final CohortUseCase cohortUseCase;
    private final CohortRestMapper cohortMapper;


    @PostMapping("cohort")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN')")
    public ResponseEntity<ApiResponse<?>> createCohort(@RequestBody CreateCohortRequest createCohortRequest) {
        try {
            Cohort cohort =
                    cohortMapper.toCohort(createCohortRequest);
            cohort = cohortUseCase.createOrEditCohort(cohort);
            CohortResponse cohortResponse =
                    cohortMapper.toCohortResponse(cohort);
            ApiResponse<CohortResponse> apiResponse = ApiResponse.<CohortResponse>builder()
                    .data(cohortResponse)
                    .message(COHORT_CREATED)
                    .statusCode(HttpStatus.OK.toString())
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (MeedlException exception) {
            return new ResponseEntity<>(new ApiResponse<>(INVALID_OPERATION, exception.getMessage(),
                    HttpStatus.BAD_REQUEST.toString()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("cohort-details")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> viewCohortDetails(
            @RequestParam @NotBlank(message = "User ID is required") String userId,
            @RequestParam @NotBlank(message = "Program ID is required") String programId,
            @RequestParam @NotBlank(message = "Cohort ID is required") String cohortId)
    {
        try {
            Cohort cohort = cohortUseCase.viewCohortDetails(userId, programId, cohortId);
            CohortResponse cohortResponse =
                    cohortMapper.toCohortResponse(cohort);
            ApiResponse<CohortResponse> apiResponse = ApiResponse.<CohortResponse>builder()
                    .data(cohortResponse)
                    .message(COHORT_VIEWED)
                    .statusCode(HttpStatus.OK.toString())
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (MeedlException exception) {
            return new ResponseEntity<>(new ApiResponse<>(INVALID_OPERATION, exception.getMessage(),
                    HttpStatus.BAD_REQUEST.toString()), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("edit-cohort")
    public ResponseEntity<ApiResponse<?>> editCohort(@RequestBody EditCohortLoanDetailRequest editCohortLoanDetailRequest) {

        try {
            Cohort cohort = cohortMapper.mapEditCohortRequestToCohort(editCohortLoanDetailRequest);
            cohortUseCase.createOrEditCohort(cohort);
            CohortResponse cohortResponse =
                    cohortMapper.toCohortResponse(cohort);
            ApiResponse<CohortResponse> apiResponse = ApiResponse.<CohortResponse>builder()
                    .data(cohortResponse)
                    .message(COHORT_EDITED)
                    .statusCode(HttpStatus.OK.toString())
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }catch (MeedlException meedlException){
            return new ResponseEntity<>(new ApiResponse<>(INVALID_OPERATION, meedlException.getMessage(),
                    HttpStatus.BAD_REQUEST.toString()), HttpStatus.BAD_REQUEST);
        }
    }


}


