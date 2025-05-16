package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramCohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.education.ProgramCohort;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramCohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.ProgramCohortMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.ProgramCohortRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ProgramCohortPersistenceAdapter implements ProgramCohortOutputPort {

    private final ProgramCohortRepository programCohortRepository;
    private final ProgramCohortMapper programCohortMapper;
    private final ProgramOutputPort programOutputPort;

    @Override
    public ProgramCohort findByCohortName(String name) {
//        programCohortRepository.findByCohortName(name);
        return null;
    }

    @Override
    public ProgramCohort findByProgramId(String programId) throws MeedlException {
        MeedlValidator.validateUUID(programId, ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        ProgramCohortEntity programCohortEntity =
                programCohortRepository.findByProgramId(programId);
        return programCohortMapper.toProgramCohort(programCohortEntity);
    }
    @Override
    public void linkCohortToProgram(Program program, Cohort savedCohort) throws MeedlException {
        ProgramCohort programCohort = new ProgramCohort();
        log.info("Linking cohort to program {}", program.getName());
        program.setNumberOfCohort(program.getNumberOfCohort() + 1);
        log.info("Service offering of organization is : {} ", program.getOrganizationIdentity().getServiceOfferings());
        programOutputPort.saveProgram(program);
        programCohort.setCohort(savedCohort);
        programCohort.setProgramId(program.getId());
        save(programCohort);
    }
    @Override
    public List<ProgramCohort> findAllByProgramId(String programId) throws MeedlException {
        MeedlValidator.validateUUID(programId, ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        List<ProgramCohortEntity> programCohortEntities =
         programCohortRepository.findAllByProgramId(programId);
        return programCohortMapper.toProgramCohortList(programCohortEntities);
    }

    @Override
    public void save(ProgramCohort programCohort) {
        log.info("The program id is second : {}", programCohort.getProgramId());
        ProgramCohortEntity programCohortEntity =
                programCohortMapper.toProgramCohortEntity(programCohort);
        log.info("mappeed program cohort entity {}", programCohortEntity.getProgramId());
        programCohortEntity = programCohortRepository.save(programCohortEntity);
    }

    @Transactional
    @Override
    public void delete(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        programCohortRepository.deleteAllByProgramId(id);
        programOutputPort.deleteProgram(id);
    }

    @Override
    public void deleteAllByCohort(CohortEntity cohort) {
        programCohortRepository.deleteAllByCohort(cohort);
    }
}
