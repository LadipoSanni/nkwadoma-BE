package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.AddProgramUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.ProgramCreateRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ProgramResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mappers.ProgramControllerMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v2/program/")
@RequiredArgsConstructor
public class ProgramController {
    private final AddProgramUseCase addProgramUseCase;
    private final ProgramControllerMapper programControllerMapper;

    @PostMapping(value = "")
    public ResponseEntity<ProgramResponse> createProgram(@RequestBody @Valid ProgramCreateRequest programCreateRequest) throws MiddlException {
        // Request to domain
        Program program = programControllerMapper.toProgram(programCreateRequest);

        program = addProgramUseCase.createProgram(program);

        // Domain to response
        return new ResponseEntity<>(programControllerMapper.toProgramResponse(program), HttpStatus.CREATED);
    }

}
