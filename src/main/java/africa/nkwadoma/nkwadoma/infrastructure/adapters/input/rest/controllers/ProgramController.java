package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.education.AddProgramUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.ProgramRestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.enums.constants.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import jakarta.ws.rs.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;

@Slf4j
@RestController
@RequestMapping(BASE_URL + "program")
@RequiredArgsConstructor
@Tag(name = "Program Controller", description = "Manage Programs in an institute")
public class ProgramController {
    private final AddProgramUseCase addProgramUseCase;
    private final ProgramRestMapper programRestMapper;

    @PostMapping("")
    @Operation(summary = "Add a proram to an Institute")
    public ResponseEntity<ApiResponse<?>> createProgram(@RequestBody @Valid ProgramCreateRequest programCreateRequest,
                                                        @AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        log.info("Meedl User ID: {}", meedlUser.getClaimAsString("sub"));
        Program program = programRestMapper.toProgram(programCreateRequest, meedlUser.getClaimAsString("sub"));

        program = addProgramUseCase.createProgram(program);

        return new ResponseEntity<>(ApiResponse.builder().statusCode(HttpStatus.CREATED.toString()).
                data(programRestMapper.toProgramResponse(program)).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).build(),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/all")
    @Operation(summary = "View all Programs in an Institute", description = "Fetch all programs in the given institute.")
    public ResponseEntity<ApiResponse<?>> viewAllPrograms(@Valid @RequestBody ProgramsRequest programsRequest)
            throws MeedlException {
        Program program = programRestMapper.toProgram(programsRequest);

        Page<Program> programs = addProgramUseCase.viewAllPrograms(program);
        List<ProgramResponse> programResponses = programs.stream().map(programRestMapper::toProgramResponse).toList();
        PaginatedResponse<ProgramResponse> response = new PaginatedResponse<>(
                programResponses, programs.hasNext(),
                programs.getTotalPages(), programsRequest.getPageSize(),
                programsRequest.getPageNumber()
        );
        return new ResponseEntity<>(ApiResponse.builder().
                statusCode(HttpStatus.OK.toString()).
                data(response).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).
                build(), HttpStatus.OK
        );
    }

    @GetMapping
    @Operation(summary = "Search a program by name")
    public ResponseEntity<ApiResponse<?>> searchProgramByName(@Valid @RequestParam(name = "name") @NotBlank(message = "Program name is required") String name)
            throws MeedlException {
        Program program = new Program();
        program.setName(name.trim());
        program = addProgramUseCase.viewProgramByName(program);

        return new ResponseEntity<>(ApiResponse.builder().statusCode(HttpStatus.OK.toString()).
                data(programRestMapper.toProgramResponse(program)).
                message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).build(),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "View a program by ID")
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
}
