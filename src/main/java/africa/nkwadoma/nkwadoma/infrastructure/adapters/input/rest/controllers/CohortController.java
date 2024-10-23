package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;


import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.CreateCohortRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.PaginatedResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education.CohortResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.education.CohortRestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.ErrorMessages.INVALID_OPERATION;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.cohort.SuccessMessages.ALL_COHORT;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.cohort.SuccessMessages.COHORT_CREATED;

@Slf4j
@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class CohortController {


    private final CohortUseCase cohortUseCase;
    private final CohortRestMapper cohortMapper;


    @PostMapping("cohort")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN')")
    public ResponseEntity<ApiResponse<?>> createCohort(@RequestBody CreateCohortRequest createCohortRequest){
        try {
            Cohort cohort =
                    cohortMapper.toCohort(createCohortRequest);
            cohort = cohortUseCase.createCohort(cohort);
            CohortResponse cohortResponse =
                    cohortMapper.toCohortResponse(cohort);
            ApiResponse<CohortResponse> apiResponse = ApiResponse.<CohortResponse>builder()
                    .body(cohortResponse)
                    .message(COHORT_CREATED)
                    .statusCode(HttpStatus.OK.toString())
                    .build();
            return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
        } catch (MeedlException exception) {
            return new ResponseEntity<>(new ApiResponse<>(INVALID_OPERATION,exception.getMessage(),
                    HttpStatus.BAD_REQUEST.toString()),HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("all-cohort")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> viewAllCohortInProgram(@RequestParam String programId,
                                                                 @RequestParam int pageSize,
                                                                 @RequestParam int pageNumber) {
        try {
            Page<Cohort> cohorts = cohortUseCase.viewAllCohortInAProgram(programId, pageSize, pageNumber);
            List<CohortResponse> cohortResponses = cohorts.stream()
                    .map(cohortMapper::toCohortResponse)
                    .toList();
            PaginatedResponse<CohortResponse> response = new PaginatedResponse<>(
                    cohortResponses, cohorts.hasNext(), cohorts.getTotalPages(), pageSize, pageNumber);

            ApiResponse<PaginatedResponse<CohortResponse>> apiResponse = ApiResponse.<PaginatedResponse<CohortResponse>>builder()
                    .statusCode(HttpStatus.OK.toString())
                    .body(response)
                    .message(ALL_COHORT)
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (MeedlException exception) {
            return new ResponseEntity<>(new ApiResponse<>(INVALID_OPERATION, exception.getMessage(),
                    HttpStatus.BAD_REQUEST.toString()), HttpStatus.BAD_REQUEST);
        }

    }
}
