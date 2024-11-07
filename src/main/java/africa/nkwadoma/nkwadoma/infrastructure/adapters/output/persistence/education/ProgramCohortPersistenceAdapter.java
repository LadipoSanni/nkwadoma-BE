package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramCohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.ProgramCohortException;
import africa.nkwadoma.nkwadoma.domain.model.education.ProgramCohort;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
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
    public ProgramCohort findByProgramId(String programId) {
        ProgramCohortEntity programCohortEntity =
                programCohortRepository.findByProgramId(programId);
        return programCohortMapper.toProgramCohort(programCohortEntity);
    }

    @Override
    public List<ProgramCohort> findAllByProgramId(String programId) throws ProgramCohortException {
        List<ProgramCohortEntity> programCohortEntities =
         programCohortRepository.findAllByProgram(programId);
        if (programCohortEntities == null){
            throw new ProgramCohortException(ProgramMessages.PROGRAM_NOT_FOUND.getMessage());
        }
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
        programCohortRepository.deleteAllByProgramId(id);
//        programOutputPort.deleteProgram(id);
    }

    @Override
    public void deleteAllByCohort(CohortEntity cohort) {
        programCohortRepository.deleteAllByCohort(cohort);
    }


}
