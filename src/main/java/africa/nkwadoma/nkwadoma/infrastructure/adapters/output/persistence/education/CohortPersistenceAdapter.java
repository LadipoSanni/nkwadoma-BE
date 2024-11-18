package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramCohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.CohortStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.CohortException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanBreakdownEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.CohortMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanBreakdownRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.*;
import java.util.List;
import java.util.stream.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages.*;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.USER_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator.validateDataElement;
import static africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator.validateUUID;


@Slf4j
@RequiredArgsConstructor
public class CohortPersistenceAdapter implements CohortOutputPort {


    private final CohortRepository cohortRepository;
    private final CohortMapper cohortMapper;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final ProgramCohortOutputPort programCohortOutputPort;
    private final LoanBreakdownRepository loanBreakdownRepository;


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

    @Transactional
    @Override
    public void deleteCohort(String id) throws MeedlException {
        MeedlValidator.validateDataElement(id);
        CohortEntity cohortEntity = cohortRepository.findById(id).orElseThrow(() -> new CohortException(COHORT_DOES_NOT_EXIST.getMessage()));
        programCohortOutputPort.deleteAllByCohort(cohortEntity);
        loanBreakdownRepository.deleteAllByCohort(cohortEntity);
        cohortRepository.deleteById(id);
    }

    @Override
    public Cohort findCohort(String cohortId) throws CohortException {
        CohortEntity cohortEntity = cohortRepository.findById(cohortId).orElseThrow(() -> new CohortException(COHORT_DOES_NOT_EXIST.getMessage()));
        return cohortMapper.toCohort(cohortEntity);
    }

    @Override
    public Cohort save(Cohort cohort) throws MeedlException {
        MeedlValidator.validateObjectInstance(cohort);
        cohort.validate();
        CohortEntity cohortEntity = cohortMapper.toCohortEntity(cohort);
        cohortEntity = cohortRepository.save(cohortEntity);
        return cohortMapper.toCohort(cohortEntity);
    }

    @Override
    public Cohort findCohortByName(String name) throws MeedlException {
        MeedlValidator.validateDataElement(name);
        CohortEntity cohortEntity = cohortRepository.findCohortByName(name);
        return cohortMapper.toCohort(cohortEntity);
    }

    private static Cohort getCohort(String cohortId, List<ProgramCohort> programCohorts) throws CohortException {
        return programCohorts.stream()
                .filter(eachCohort -> eachCohort.getCohort().getId().equals(cohortId))
                .findFirst()
                .orElseThrow(() -> new CohortException(COHORT_DOES_NOT_EXIST.getMessage())).getCohort();
    }

    @Override
    public Cohort searchForCohortInAProgram(String name, String programId) throws MeedlException {
        List<ProgramCohort> programCohorts = programCohortOutputPort.findAllByProgramId(programId);
        return programCohorts.stream()
                .filter(eachProgramCohort -> eachProgramCohort.getCohort().getName().equalsIgnoreCase(name.trim()))
                .findFirst().orElseThrow(() -> new CohortException(COHORT_DOES_NOT_EXIST.getMessage())).getCohort();
    }

    @Override
    public List<Cohort> findAllCohortInAProgram(String programId) throws MeedlException {
        validateUUID(programId);
        List<CohortEntity> cohortEntities = cohortRepository.findAllByProgramId(programId);
        return cohortMapper.toCohortList(cohortEntities);
    }

}

