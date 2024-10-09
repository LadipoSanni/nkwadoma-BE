package africa.nkwadoma.nkwadoma.infrastructure.adapters.output;

import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.ProgramMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.TrainingInstituteEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.ProgramRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.TrainingInstituteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.ORGANIZATION_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages.PROGRAM_NOT_FOUND;

@RequiredArgsConstructor
@Component
@Slf4j
public class ProgramPersistenceAdapter implements ProgramOutputPort {
    private final ProgramRepository programRepository;
    private final TrainingInstituteRepository trainingInstituteRepository;
    private final ProgramMapper programMapper;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final OrganizationIdentityMapper organizationIdentityMapper;

    @Override
    public Optional<Program> findProgramByName(String programName) {
        Optional<ProgramEntity> programEntity = programRepository.findByName(programName);
        if (programEntity.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(programMapper.toProgram(programEntity.get()));
    }

    @Override
    public Program saveProgram(Program program) throws MiddlException {
        OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(program.getOrganizationId()).
                orElseThrow(() -> new ResourceNotFoundException(ORGANIZATION_NOT_FOUND.getMessage()));
        ProgramEntity programEntity = programMapper.toProgramEntity(program);

        TrainingInstituteEntity instituteEntity = TrainingInstituteEntity.builder().
                organizationEntity(organizationIdentityMapper.toOrganizationEntity(organizationIdentity)).
                numberOfPrograms(+1).build();
        instituteEntity = trainingInstituteRepository.save(instituteEntity);

        programEntity.setTrainingInstituteEntity(instituteEntity);
        programEntity = programRepository.save(programEntity);

        return programMapper.toProgram(programEntity);
    }
}
