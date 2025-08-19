package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.constants.ControllerConstant;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.PaginatedResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("program")
@RequiredArgsConstructor
@Tag(name = "Program Controller", description = "Manage Programs in an institute")
public class ProgramController {
    private final AddProgramUseCase addProgramUseCase;
    private final ProgramRestMapper programRestMapper;

    @PostMapping("")
    @Operation(summary = "Add a program to an Institute")
    @PreAuthorize("hasRole('ORGANIZATION_SUPER_ADMIN') or hasRole('ORGANIZATION_ADMIN')")
    public ResponseEntity<ApiResponse<?>> createProgram(@RequestBody @Valid ProgramCreateRequest programCreateRequest,
                                                        @AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        log.info("Creating program is Meedl User with ID: {}", meedlUser.getClaimAsString("sub"));
        Program program = programRestMapper.toProgram(programCreateRequest, meedlUser.getClaimAsString("sub"));

        program = addProgramUseCase.createProgram(program);

        return new ResponseEntity<>(ApiResponse.builder().statusCode(HttpStatus.CREATED.toString()).
                data(programRestMapper.toProgramResponse(program)).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).build(),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/programs/all")
    @PreAuthorize("hasRole('MEEDL_SUPER_ADMIN') or hasRole('ORGANIZATION_SUPER_ADMIN') or hasRole('ORGANIZATION_ADMIN') or hasRole('PORTFOLIO_MANAGER')")
    @Operation(summary = "View all Programs in an Institute", description = "Fetch all programs in the given organization.")
    public ResponseEntity<ApiResponse<?>> viewAllPrograms(@AuthenticationPrincipal Jwt meedlUser,
                                                          @RequestParam(name = "organizationId", required = false) String organizationId,
                                                          @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) throws MeedlException {
        Program program = new Program();
        program.setPageSize(pageSize);
        program.setPageNumber(pageNumber);
        program.setOrganizationId(organizationId);
        log.info("Meedl User ID: {}", meedlUser.getClaimAsString("sub"));
        program.setCreatedBy(meedlUser.getClaimAsString("sub"));

        log.info("organizationId from param {}", organizationId);
        log.info("organizationId from programId {}", program.getOrganizationId());

        Page<Program> programs = addProgramUseCase.viewAllPrograms(program);
        log.info("Programs returned from db: {}", programs);
        List<ProgramResponse> programResponses = programs.stream().map(programRestMapper::toProgramResponse).toList();
        log.info("Programs mapped: {}", programResponses);
        PaginatedResponse<ProgramResponse> response = new PaginatedResponse<>(
                programResponses, programs.hasNext(),
                programs.getTotalPages(), programs.getTotalElements(),pageNumber, pageSize
        );
        return new ResponseEntity<>(ApiResponse.builder().
                statusCode(HttpStatus.OK.toString()).
                data(response).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                build(), HttpStatus.OK
        );
    }

    @GetMapping("/search")
    @Operation(summary = "Search a program by name")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('ORGANIZATION_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<?>> searchProgramByName
            (@Valid @RequestParam(name = "name") @NotBlank(message = "Program name is required") String name,
             @AuthenticationPrincipal Jwt meedlUser,
             @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
             @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {
        Program program = Program.builder().name(name.trim()).createdBy(meedlUser.getClaimAsString("sub"))
                .pageNumber(pageNumber).pageSize(pageSize).build();
        log.info("Program search parameters: {}", program);

        Page<Program> programs = addProgramUseCase.searchProgramByName(program);
        List<ProgramResponse> programResponses = programs.stream().
                map(programRestMapper::toProgramResponse).toList();
        PaginatedResponse<ProgramResponse> response = new PaginatedResponse<>(
                programResponses, programs.hasNext(),
                programs.getTotalPages(), programs.getTotalElements(),pageNumber, pageSize
        );
        return new ResponseEntity<>(ApiResponse.builder().
                statusCode(HttpStatus.OK.name()).
                data(response).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).build(),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "View a program by ID")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('ORGANIZATION_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<?>> viewProgramByID(@PathVariable @Valid @NotBlank(message = "Program ID is required") String id)
            throws MeedlException {
        Program program = new Program();
        program.setId(id.trim());
        program = addProgramUseCase.viewProgramById(program);

        return new ResponseEntity<>(ApiResponse.builder().statusCode(HttpStatus.OK.toString()).
                data(programRestMapper.toProgramResponse(program)).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).build(),
                HttpStatus.OK
        );
    }

    @PatchMapping("/edit")
    @Operation(summary = "Update an existing program")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('ORGANIZATION_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateProgram(@RequestBody @Valid ProgramUpdateRequest programUpdateRequest,
                                                        @AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        Program program = programRestMapper.toUpdatedProgram(programUpdateRequest);
        program.setCreatedBy(meedlUser.getClaim("sub"));
        log.info("Program at controller level: ========>{}", program);
        program = addProgramUseCase.updateProgram(program);

        return new ResponseEntity<>(ApiResponse.builder().statusCode(HttpStatus.OK.toString()).
                data(programRestMapper.toProgramResponse(program)).
                message(String.format("Program %s", ControllerConstant.UPDATED_SUCCESSFULLY.getMessage())).build(),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete a program by it's ID")
    @PreAuthorize("hasRole('ORGANIZATION_ADMIN') or hasRole('ORGANIZATION_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<?>> deleteProgram(@PathVariable @Valid @NotBlank(message = "Program id is required") String id)
            throws MeedlException {
        Program program = new Program();
        program.setId(id.trim());
        addProgramUseCase.deleteProgram(program);

        return new ResponseEntity<>(ApiResponse.builder().
                statusCode(HttpStatus.OK.toString()).
                message("Program " + ControllerConstant.DELETED_SUCCESSFULLY.getMessage()).build(),
                HttpStatus.OK
        );
    }
}
