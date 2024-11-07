package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramCohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.CohortStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.CohortException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.education.ProgramCohort;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.CohortMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages.*;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.USER_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator.validateDataElement;
import static java.util.stream.Collectors.toList;


@Slf4j
@RequiredArgsConstructor
public class CohortPersistenceAdapter implements CohortOutputPort {

    private final ProgramOutputPort programOutputPort;
    private final CohortRepository cohortRepository;
    private final CohortMapper cohortMapper;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final ProgramCohortOutputPort programCohortOutputPort;


    @Override
    public Cohort saveCohort(Cohort cohort) throws MeedlException {
        if (ObjectUtils.isEmpty(cohort)) {
            throw new EducationException(MeedlMessages.INVALID_REQUEST.getMessage());
        }
        cohort.validate();
        Program program = programOutputPort.findProgramById(cohort.getProgramId());
        if (program == null) {
            throw new CohortException(ProgramMessages.PROGRAM_NOT_FOUND.getMessage());
        }
        List<ProgramCohort> programCohortList = programCohortOutputPort.findAllByProgramId(cohort.getProgramId());
        Optional<ProgramCohort> existingProgramCohort = programCohortList.stream()
                .filter(eachProgramCohort -> eachProgramCohort.getCohort().getName().equals(cohort.getName()))
                .findFirst();
        Cohort retrievedCohort  =  updateOrAddCohortToProgram(cohort, existingProgramCohort, program);
         programOutputPort.saveProgram(program);
        return retrievedCohort;
    }

    private Cohort updateOrAddCohortToProgram(Cohort cohort, Optional<ProgramCohort> existingProgramCohort, Program program) throws MeedlException {
        CohortEntity cohortEntity;
        if (existingProgramCohort.isPresent() && existingProgramCohort.get().getCohort() != null) {
            Cohort cohortToUpdate = existingProgramCohort.get().getCohort();

            if (cohort.getId() != null && cohort.getId().equals(cohortToUpdate.getId())) {
                cohortToUpdate = cohortMapper.cohortToUpdateCohort(cohort);
                cohortToUpdate.setUpdatedAt(LocalDateTime.now());
                activateStatus(cohortToUpdate);
                cohortEntity = cohortMapper.toCohortEntity(cohortToUpdate);
                cohortRepository.save(cohortEntity);

            } else {
                throw new CohortException(COHORT_EXIST.getMessage());
            }
        } else {
            cohort.setCreatedAt(LocalDateTime.now());
            activateStatus(cohort);
            ProgramCohort newProgramCohort = new ProgramCohort();
            cohortEntity = cohortMapper.toCohortEntity(cohort);
            cohortRepository.save(cohortEntity);
            cohort = cohortMapper.toCohort(cohortEntity);
            program.setNumberOfCohort(program.getNumberOfCohort() + 1);

            newProgramCohort.setCohort(cohort);
            newProgramCohort.setProgram(program.getId());
            programCohortOutputPort.save(newProgramCohort);
        }
        return cohortMapper.toCohort(cohortEntity);
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
        UserIdentity userIdentity = userIdentityOutputPort.findById(userId);
        if (userIdentity == null){
            throw new IdentityException(USER_NOT_FOUND.getMessage());
        }
        List<ProgramCohort> programCohorts = programCohortOutputPort.findAllByProgramId(programId);
        return getCohort(cohortId,programCohorts );
    }

    private static Cohort getCohort(String cohortId, List<ProgramCohort> programCohorts) throws CohortException {
        return programCohorts.stream()
                .filter(eachCohort -> eachCohort.getCohort().getId().equals(cohortId))
                .findFirst()
                .orElseThrow(() -> new CohortException(COHORT_DOES_NOT_EXIST.getMessage())).getCohort();
    }

    @Override
    public List<Cohort> findAllCohortInAProgram(String programId) throws MeedlException {
        List<ProgramCohort> programCohorts = programCohortOutputPort.findAllByProgramId(programId);
        return programCohorts.stream()
                .map(ProgramCohort::getCohort)
                .toList();
//        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
//        int start = (int) pageRequest.getOffset();
//        int end = Math.min((start + pageRequest.getPageSize()), cohorts.size());
//        if (start >= cohorts.size()) {
//            return new PageImpl<>(Collections.emptyList(), pageRequest, cohorts.size());
//        }
//        return new PageImpl<>(cohorts.subList(start, end), pageRequest, cohorts.size());
    }

}

