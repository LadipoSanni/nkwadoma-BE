package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.CohortStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.CohortMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortRepository;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.CohortExistException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages.COHORT_EXIST;


@Slf4j
@RequiredArgsConstructor
public class CohortPersistenceAdapter implements CohortOutputPort {

    private final ProgramOutputPort programOutputPort;
    private final CohortRepository cohortRepository;
    private final CohortMapper cohortMapper;


    @Override
    public Cohort saveCohort(Cohort cohort) throws MeedlException {
        if (ObjectUtils.isEmpty(cohort)) {
            throw new EducationException(MeedlMessages.INVALID_REQUEST.getMessage());
        }
        cohort.validate();
        Program program = programOutputPort.findProgramById(cohort.getProgramId());

        boolean cohortExists = program.getCohorts().stream()
                .anyMatch(eachCohort -> eachCohort.getName().equals(cohort.getName()));
        if (cohortExists) {
            throw new CohortExistException(COHORT_EXIST.getMessage());
        } else {
            program.getCohorts().add(cohort);
            program.setNumberOfCohort(program.getNumberOfCohort()+1);
            cohort.setCreatedAt(LocalDateTime.now());
            programOutputPort.saveProgram(program);
        }
        CohortEntity cohortEntity = cohortMapper.toCohortEntity(cohort);
        return cohortMapper.toCohort(cohortEntity);
    }
}
