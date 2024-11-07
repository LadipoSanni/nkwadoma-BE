package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;


import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.CreateCohortRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.EditCohortLoanDetailRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education.CohortResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.education.CohortRestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.enums.constants.ControllerConstant;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public ResponseEntity<ApiResponse<?>> createCohort(@RequestBody CreateCohortRequest createCohortRequest) throws MeedlException {
            Cohort cohort = cohortMapper.toCohort(createCohortRequest);
            cohort = cohortUseCase.createOrEditCohort(cohort);
            CohortResponse cohortResponse = cohortMapper.toCohortResponse(cohort);
            ApiResponse<CohortResponse> apiResponse = ApiResponse.<CohortResponse>builder()
                    .data(cohortResponse)
                    .message(COHORT_CREATED)
                    .statusCode(HttpStatus.OK.toString())
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping("cohort-details")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> viewCohortDetails(
            @RequestParam @NotBlank(message = "User ID is required") String userId,
            @RequestParam @NotBlank(message = "Program ID is required") String programId,
            @RequestParam @NotBlank(message = "Cohort ID is required") String cohortId) throws MeedlException {
        Cohort cohort = cohortUseCase.viewCohortDetails(userId, programId, cohortId);
        CohortResponse cohortResponse =
                cohortMapper.toCohortResponse(cohort);
        ApiResponse<CohortResponse> apiResponse = ApiResponse.<CohortResponse>builder()
                .data(cohortResponse)
                .message(COHORT_VIEWED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @PostMapping("cohort/edit")
    public ResponseEntity<ApiResponse<?>> editCohort(@RequestBody EditCohortLoanDetailRequest editCohortLoanDetailRequest) throws MeedlException {
        Cohort cohort = cohortMapper.mapEditCohortRequestToCohort(editCohortLoanDetailRequest);
        cohortUseCase.createOrEditCohort(cohort);
        CohortResponse cohortResponse =
                cohortMapper.toCohortResponse(cohort);
        ApiResponse<CohortResponse> apiResponse = ApiResponse.<CohortResponse>builder()
                .data(cohortResponse)
                .message(COHORT_EDITED_SUCCESSFULLY)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete a cohort by it's ID")
    public ResponseEntity<ApiResponse<?>> deleteCohort(@PathVariable @Valid @NotBlank(message = "Cohort id is required") String id)
            throws MeedlException {
        cohortUseCase.deleteCohort(id);
        return new ResponseEntity<>(ApiResponse.builder().
                statusCode(HttpStatus.OK.toString()).
                message("Cohort " + ControllerConstant.DELETED_SUCCESSFULLY.getMessage()).build(),
                HttpStatus.OK
        );
    }

    @GetMapping("invite-cohort")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> inviteCohort(
            @AuthenticationPrincipal Jwt meedl,
            @RequestParam @NotBlank(message = "Program ID is required") String programId,
            @RequestParam @NotBlank(message = "Cohort ID is required") String cohortId) throws MeedlException {
        cohortUseCase.inviteCohort(meedl.getClaimAsString("sub"), programId, cohortId);
        return new ResponseEntity<>(ApiResponse.<String>builder()
                .message(COHORT_INVITED)
                .statusCode(HttpStatus.OK.toString())
                .build(), HttpStatus.OK);
    }

    @GetMapping("searchCohort")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> searchCohortInAProgram(
            @RequestParam @NotBlank(message = "Cohort name is required") String cohortName,
            @RequestParam @NotBlank(message = "Program ID is required") String programId){

        try {
            Cohort cohort = cohortUseCase.searchForCohortInAProgram(cohortName, programId);
            CohortResponse cohortResponse =
                    cohortMapper.toCohortResponse(cohort);
            ApiResponse<CohortResponse> apiResponse = ApiResponse.<CohortResponse>builder()
                    .data(cohortResponse)
                    .message(COHORT_RETRIEVED)
                    .statusCode(HttpStatus.OK.toString())
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (MeedlException exception) {
            return new ResponseEntity<>(new ApiResponse<>(INVALID_OPERATION, exception.getMessage(),
                    HttpStatus.BAD_REQUEST.toString()), HttpStatus.BAD_REQUEST);
        }
    }

}


