package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.education.AddProgramUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.ProgramCreateRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education.ProgramResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.ProgramRestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.*;
import org.springframework.security.core.annotation.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.*;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;

@Slf4j
@RestController
@RequestMapping(BASE_URL + "program")
@RequiredArgsConstructor
public class ProgramController {
    private final AddProgramUseCase addProgramUseCase;
    private final ProgramRestMapper programRestMapper;

    @PostMapping("")
    public ResponseEntity<ProgramResponse> createProgram(@RequestBody @Valid ProgramCreateRequest programCreateRequest,
                                                         @AuthenticationPrincipal Jwt middlUser) throws MiddlException {
        // Request to domain
        Program program = programRestMapper.toProgram(programCreateRequest, middlUser.getClaimAsString("sub"));
        log.info("Mapped Program create request: {}, {}", program.toString(), middlUser.getClaimAsString("sub"));

        program = addProgramUseCase.createProgram(program);

        // Domain to response
        return new ResponseEntity<>(programRestMapper.toProgramResponse(program), HttpStatus.CREATED);
    }

}
