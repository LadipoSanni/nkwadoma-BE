package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramCohortOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.education.ProgramCohort;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramCohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.ProgramCohortMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.ProgramCohortRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ProgramCohortPersistenceAdapter implements ProgramCohortOutputPort {

    private final ProgramCohortRepository programCohortRepository;
    private final ProgramCohortMapper programCohortMapper;

    @Override
    public ProgramCohort findByCohortName(String name) {
//        programCohortRepository.findByCohortName(name);
        return null;
    }

    @Override
    public ProgramCohort findByProgramId(String programId) {
        ProgramCohortEntity programCohortEntity =
                programCohortRepository.findByProgram(programId);
        return programCohortMapper.toProgramCohort(programCohortEntity);
    }

    @Override
    public List<ProgramCohort> findAllByProgramId(String programId) {
        List<ProgramCohortEntity> programCohortEntities =
         programCohortRepository.findAllByProgram(programId);
        return programCohortMapper.toProgramCohortList(programCohortEntities);
    }

    @Override
    public void save(ProgramCohort programCohort1) {
        ProgramCohortEntity programCohortEntity =
                programCohortMapper.toProgramCohortEntity(programCohort1);
        programCohortRepository.save(programCohortEntity);

    }
}
