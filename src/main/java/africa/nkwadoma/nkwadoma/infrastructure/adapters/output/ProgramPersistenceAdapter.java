package africa.nkwadoma.nkwadoma.infrastructure.adapters.output;

import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.ProgramMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.OrganizationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.TrainingInstituteEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.OrganizationEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.ProgramRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.TrainingInstituteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.ORGANIZATION_NOT_FOUND;

@RequiredArgsConstructor
@Component
@Slf4j
public class ProgramPersistenceAdapter implements ProgramOutputPort {
    private final ProgramRepository programRepository;
    private final TrainingInstituteRepository trainingInstituteRepository;
    private final ProgramMapper programMapper;
    private final OrganizationEntityRepository organizationEntityRepository;

    @Override
    public Program saveProgram(Program program) throws ResourceNotFoundException, ResourceAlreadyExistsException {
        if (programRepository.findByName(program.getName()).isPresent())
            throw new ResourceAlreadyExistsException(ProgramMessages.PROGRAM_ALREADY_EXISTS.getMessage());
        ProgramEntity programEntity = programMapper.toProgramEntity(program);
        OrganizationEntity organizationEntity =
                organizationEntityRepository.findById(program.getOrganizationId()).
                        orElseThrow(()-> new ResourceNotFoundException(ORGANIZATION_NOT_FOUND.getMessage()));
        TrainingInstituteEntity institute = TrainingInstituteEntity.builder().build();
        institute.setOrganizationEntity(organizationEntity);
        institute.setNumberOfPrograms(institute.getNumberOfPrograms() + 1);
        trainingInstituteRepository.save(institute);

        programRepository.save(programEntity);


        return programMapper.toProgram(programEntity);
    }
}
