package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.CohortStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.CohortException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.CohortMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages.*;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.USER_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator.validateDataElement;


@Slf4j
@RequiredArgsConstructor
public class CohortPersistenceAdapter implements CohortOutputPort {

    private final ProgramOutputPort programOutputPort;
    private final CohortRepository cohortRepository;
    private final CohortMapper cohortMapper;
    private final UserIdentityOutputPort userIdentityOutputPort;


    @Override
    public Cohort saveCohort(Cohort cohort) throws MeedlException {
        if (ObjectUtils.isEmpty(cohort)) {
            throw new EducationException(MeedlMessages.INVALID_REQUEST.getMessage());
        }
        cohort.validate();
        Program program = programOutputPort.findProgramById(cohort.getProgramId());
        Optional<Cohort> existingCohort = program.getCohorts().stream()
                .filter(eachCohort -> eachCohort.getName().equals(cohort.getName()))
                .findFirst();
        updateOrAddCohortToProgram(cohort, existingCohort, program);
        Program savedProgram = programOutputPort.saveProgram(program);
        Optional<Cohort> retrievedCohort = retrieveCohortFromProgram(cohort, savedProgram);
        CohortEntity cohortEntity = cohortMapper.toCohortEntity(retrievedCohort.get());
        return cohortMapper.toCohort(cohortEntity);
    }

    private void updateOrAddCohortToProgram(Cohort cohort, Optional<Cohort> existingCohort, Program program) throws CohortException {
        if (existingCohort.isPresent()) {
            Cohort cohortToUpdate = existingCohort.get();
            if (cohort.getId() != null && cohort.getId().equals(cohortToUpdate.getId())) {
                cohortToUpdate =  cohortMapper.cohortToUpdateCohort(cohort);
                cohortToUpdate.setUpdatedAt(LocalDateTime.now());
                activateStatus(cohortToUpdate);
            } else {
                throw new CohortException(COHORT_EXIST.getMessage());
            }
        } else {
            cohort.setCreatedAt(LocalDateTime.now());
            activateStatus(cohort);
            program.getCohorts().add(cohort);
            program.setNumberOfCohort(program.getNumberOfCohort() + 1);
        }
    }

    private static Optional<Cohort> retrieveCohortFromProgram(Cohort cohort, Program program) {
        return program.getCohorts().stream()
                .filter(cohort1 -> cohort1.getName().equals(cohort.getName()))
                .findFirst();
    }

    private static void activateStatus(Cohort cohort) {
        LocalDateTime now = LocalDateTime.now();
        if (cohort.getStartDate().isAfter(now)) {
            cohort.setActivationStatus(ActivationStatus.INACTIVE);
            cohort.setCohortStatus(CohortStatus.INCOMING);
        } else if (cohort.getStartDate().isBefore(now) && cohort.getExpectedEndDate().isAfter(now)) {
            cohort.setActivationStatus(ActivationStatus.ACTIVE);
            cohort.setCohortStatus(CohortStatus.CURRENT);
        } else if (cohort.getExpectedEndDate().isBefore(now) || cohort.getExpectedEndDate().isEqual(now)) {
            cohort.setActivationStatus(ActivationStatus.INACTIVE);
            cohort.setCohortStatus(CohortStatus.GRADUATED);
        }
    }

    @Override
    public Cohort viewCohortDetails(String userId, String programId, String cohortId) throws MeedlException {
        validateDataElement(userId);
        validateDataElement(programId);
        validateDataElement(cohortId);
//        UserIdentity userIdentity = userIdentityOutputPort.findById(userId);
//        if (userIdentity == null){
//            throw new IdentityException(USER_NOT_FOUND.getMessage());
//        }
        Program program = programOutputPort.findProgramById(programId);
        return getCohort(cohortId, program);
    }

    private static Cohort getCohort(String cohortId, Program program) throws CohortException {
        return program.getCohorts().stream()
                .filter(eachCohort -> eachCohort.getId().equals(cohortId))
                .findFirst()
                .orElseThrow(() -> new CohortException(COHORT_DOES_NOT_EXIST.getMessage()));
    }

}

