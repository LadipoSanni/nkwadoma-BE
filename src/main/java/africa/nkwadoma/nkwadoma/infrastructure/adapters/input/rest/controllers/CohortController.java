package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;


import africa.nkwadoma.nkwadoma.application.ports.input.education.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.enums.constants.*;
import io.swagger.v3.oas.annotations.*;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.security.core.annotation.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.*;
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
        cohortResponse.setLoanBreakdowns(cohort.getLoanBreakdowns().stream()
                .map(cohortMapper::toLoanBreakdownResponse)
                .toList());
        ApiResponse<CohortResponse> apiResponse = ApiResponse.<CohortResponse>builder()
                .data(cohortResponse)
                .message(COHORT_CREATED)
                .status(HttpStatus.OK.toString())
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
                .status(HttpStatus.OK.toString())
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
                .status(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete a cohort by it's ID")
    public ResponseEntity<ApiResponse<?>> deleteCohort(@PathVariable @Valid @NotBlank(message = "Cohort id is required") String id)
            throws MeedlException {
        cohortUseCase.deleteCohort(id);
        return new ResponseEntity<>(ApiResponse.builder().
                status(HttpStatus.OK.toString()).
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
                .status(HttpStatus.OK.toString())
                .build(), HttpStatus.OK);
    }

    @GetMapping("searchCohort")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> searchCohortInAProgram(
            @RequestParam @NotBlank(message = "Cohort name is required") String cohortName,
            @RequestParam @NotBlank(message = "Program ID is required") String programId) throws MeedlException {
        Cohort cohort = cohortUseCase.searchForCohortInAProgram(cohortName, programId);
        CohortResponse cohortResponse =
                cohortMapper.toCohortResponse(cohort);
        ApiResponse<CohortResponse> apiResponse = ApiResponse.<CohortResponse>builder()
                .data(cohortResponse)
                .message(COHORT_RETRIEVED)
                .status(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("{programId}/cohorts")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<PaginatedResponse<CohortResponse>>> viewAllCohortsInAProgram(
            @PathVariable @NotBlank(message = "Program ID is required") String programId,
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "10", required = false) int pageSize) throws MeedlException {
        Page<Cohort> cohorts = cohortUseCase.viewAllCohortInAProgram(programId, pageSize, pageNumber);
        List<CohortResponse> cohortResponses = cohorts.stream().map(cohortMapper::toCohortResponse).toList();
        PaginatedResponse<CohortResponse> paginatedResponse = new PaginatedResponse<>(
                cohortResponses, cohorts.hasNext(), cohorts.getTotalPages(), pageNumber, pageSize);
        ApiResponse<PaginatedResponse<CohortResponse>> apiResponse = ApiResponse.<PaginatedResponse<CohortResponse>>builder()
                .data(paginatedResponse)
                .message(String.format("Cohorts %s", ControllerConstant.RETURNED_SUCCESSFULLY))
                .statusCode(200)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
