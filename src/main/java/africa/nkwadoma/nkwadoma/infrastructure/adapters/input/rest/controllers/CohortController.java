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

    @PostMapping("cohort/create")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN')")
    public ResponseEntity<ApiResponse<?>> createCohort(@AuthenticationPrincipal Jwt meedlUser, @RequestBody @Valid
            CreateCohortRequest createCohortRequest) throws MeedlException {
        Cohort cohort = cohortMapper.toCohort(createCohortRequest);
        cohort.setCreatedBy(meedlUser.getClaimAsString("sub"));
        cohort = cohortUseCase.createCohort(cohort);
        CohortResponse cohortResponse = cohortMapper.toCohortResponse(cohort);
        cohortResponse.setLoanBreakdowns(cohort.getLoanBreakdowns().stream()
                .map(cohortMapper::toLoanBreakdownResponse)
                .toList());
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
            @AuthenticationPrincipal Jwt meedlUser,
            @RequestParam @NotBlank(message = "Cohort ID is required") String cohortId) throws MeedlException {

        Cohort cohort = cohortUseCase.viewCohortDetails(meedlUser.getClaimAsString("sub"), cohortId);
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
    public ResponseEntity<ApiResponse<?>> editCohort(@AuthenticationPrincipal Jwt meedlUser, @RequestBody @Valid
                                          EditCohortLoanDetailRequest editCohortLoanDetailRequest) throws MeedlException {
        Cohort cohort = cohortMapper.mapEditCohortRequestToCohort(editCohortLoanDetailRequest);
        cohort.setUpdatedBy(meedlUser.getClaimAsString("sub"));
        cohort = cohortUseCase.editCohort(cohort);
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

    @PostMapping("cohort/loanee/refer")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> inviteCohort(
            @AuthenticationPrincipal Jwt meedl,
            @RequestBody ReferCohortRequest referCohortRequest) throws MeedlException {
        String message = cohortUseCase.
                inviteCohort(meedl.getClaimAsString("sub"),
                referCohortRequest.getCohortId(),
                referCohortRequest.getLoaneeIds());
        return new ResponseEntity<>(ApiResponse.<String>builder()
                .message(message)
                .statusCode(HttpStatus.OK.toString())
                .build(), HttpStatus.OK);
    }
    @GetMapping("program/searchCohort")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> searchCohortInAProgram(
            @RequestParam @NotBlank(message = "Cohort name is required") String cohortName,
            @RequestParam @NotBlank(message = "Program ID is required") String programId) throws MeedlException {
        List<Cohort> cohorts = cohortUseCase.searchForCohortInAProgram(cohortName, programId);
        List<CohortResponse> cohortResponses =
                cohortMapper.toCohortResponses(cohorts);
        ApiResponse<List<CohortResponse>> apiResponse = ApiResponse.<List<CohortResponse>>builder()
                .data(cohortResponses)
                .message(COHORT_RETRIEVED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("searchCohort")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<?>> searchCohort(
            @AuthenticationPrincipal Jwt meedl,
            @RequestParam @NotBlank(message = "Cohort name is required") String cohortName) throws MeedlException {
        List<Cohort> cohorts = cohortUseCase.searchForCohort(meedl.getClaimAsString("sub"),cohortName);
        List<CohortResponse> cohortResponses =  cohortMapper.toCohortResponses(cohorts);
        ApiResponse<List<CohortResponse>> apiResponse = ApiResponse.<List<CohortResponse>>builder()
                .data(cohortResponses)
                .message(COHORT_RETRIEVED)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("cohort/all")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    public ResponseEntity<ApiResponse<PaginatedResponse<CohortResponse>>> viewAllCohortsInAProgram(
            @RequestParam @NotBlank(message = "Program ID is required") String programId,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {
        Page<Cohort> cohorts = cohortUseCase.viewAllCohortInAProgram(programId, pageSize, pageNumber);
        List<CohortResponse> cohortResponses = cohorts.stream().map(cohortMapper::toCohortResponse).toList();
        PaginatedResponse<CohortResponse> paginatedResponse = new PaginatedResponse<>(
                cohortResponses, cohorts.hasNext(), cohorts.getTotalPages(), pageNumber, pageSize);
        ApiResponse<PaginatedResponse<CohortResponse>> apiResponse = ApiResponse.<PaginatedResponse<CohortResponse>>builder()
                .data(paginatedResponse)
                .message(String.format("Cohorts %s", ControllerConstant.RETURNED_SUCCESSFULLY.getMessage()))
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @GetMapping("organization-cohort/all")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<CohortResponse>>> viewAllCohortsInOrganization(
            @AuthenticationPrincipal Jwt meedl,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {
        Page<Cohort> cohorts = cohortUseCase.viewAllCohortInOrganization(meedl.getClaimAsString("sub"),
                                                                         pageNumber,pageSize);
        List<CohortResponse> cohortResponses = cohorts.stream().map(cohortMapper::toCohortResponse).toList();
        PaginatedResponse<CohortResponse> paginatedResponse = new PaginatedResponse<>(
                cohortResponses, cohorts.hasNext(), cohorts.getTotalPages(), pageNumber,pageSize);
        ApiResponse<PaginatedResponse<CohortResponse>> apiResponse = ApiResponse.<PaginatedResponse<CohortResponse>>builder()
                .data(paginatedResponse)
                .message(String.format("Cohorts %s", ControllerConstant.RETURNED_SUCCESSFULLY.getMessage()))
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
