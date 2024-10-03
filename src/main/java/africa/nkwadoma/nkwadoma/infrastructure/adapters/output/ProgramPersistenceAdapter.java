package africa.nkwadoma.nkwadoma.infrastructure.adapters.output;

import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.ProgramException;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.ProgramMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.OrganizationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.TrainingInstituteEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.OrganizationEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.ProgramRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.TrainingInstituteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ProgramPersistenceAdapter implements ProgramOutputPort {
    private final ProgramRepository programRepository;
    private final TrainingInstituteRepository trainingInstituteRepository;
    private final ProgramMapper programMapper;
    private final OrganizationEntityRepository organizationEntityRepository;

    @Override
    public Program saveProgram(Program program) throws ProgramException {
        if (programRepository.findByName(program.getName()).isPresent())
            throw new ProgramException(ProgramMessages.PROGRAM_ALREADY_EXISTS.getMessage());
        ProgramEntity programEntity = programMapper.toProgramEntity(program);
        programRepository.save(programEntity);

        TrainingInstituteEntity institute = programEntity.getTrainingInstituteEntity();
        Optional<OrganizationEntity> organizationEntity =
                organizationEntityRepository.findById(program.getOrganizationId());
        organizationEntity.ifPresent(institute::setOrganizationEntity);
        institute.setNumberOfPrograms(institute.getNumberOfPrograms() + 1);
        trainingInstituteRepository.save(institute);

        return programMapper.toProgram(programEntity);
    }
}
