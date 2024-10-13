package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.education.AddProgramUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.ProgramCreateRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.ProgramRestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.enums.constants.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.ErrorMessages.INVALID_OPERATION;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;

@Slf4j
@RestController
@RequestMapping(BASE_URL + "program")
@RequiredArgsConstructor
public class ProgramController {
    private final AddProgramUseCase addProgramUseCase;
    private final ProgramRestMapper programRestMapper;

    @PostMapping("")
    public ResponseEntity<ApiResponse<?>> createProgram(@RequestBody @Valid ProgramCreateRequest programCreateRequest,
                                                        @AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
            Program program = programRestMapper.toProgram(programCreateRequest, meedlUser.getClaimAsString("sub"));
            log.info("Mapped Program create request: {}, {}", program.toString(), meedlUser.getClaimAsString("sub"));

            program = addProgramUseCase.createProgram(program);

            return new ResponseEntity<>(ApiResponse.builder().statusCode(HttpStatus.CREATED.toString()).
                    body(programRestMapper.toProgramResponse(program)).
                    message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage()).build(),
                    HttpStatus.CREATED);
    }

}
