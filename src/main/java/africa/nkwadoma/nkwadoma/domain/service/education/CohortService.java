package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.*;

import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages.PROGRAM_NOT_FOUND;

import java.util.List;

@RequiredArgsConstructor
public class CohortService implements CohortUseCase {

    private final CohortOutputPort cohortOutputPort;
    private final CohortLoaneeOutputPort cohortLoaneeOutputPort;
    private final ProgramOutputPort programOutputPort;

    @Override
    public Cohort createOrEditCohort(Cohort cohort) throws MeedlException {
        return cohortOutputPort.saveCohort(cohort);
    }

    @Override
    public Cohort viewCohortDetails(String userId, String programId, String cohortId) throws MeedlException {
        return cohortOutputPort.viewCohortDetails(userId,programId,cohortId);
    }

    @Override
    public Page<Cohort> viewAllCohortInAProgram(Cohort cohort) throws MeedlException {
        MeedlValidator.validateObjectInstance(cohort);
        MeedlValidator.validateDataElement(cohort.getProgramId());
        String programId = cohort.getProgramId().trim();
        MeedlValidator.validateUUID(programId);
        MeedlValidator.validatePageNumber(cohort.getPageNumber());
        MeedlValidator.validatePageSize(cohort.getPageSize());
        Program foundProgram = programOutputPort.findProgramById(programId);
        if (ObjectUtils.isEmpty(foundProgram)) {
            throw new MeedlException(PROGRAM_NOT_FOUND.getMessage());
        }
        List<Cohort> cohorts = cohortOutputPort.findAllCohortInAProgram(programId);
        Pageable pageRequest = PageRequest.of(cohort.getPageNumber(), cohort.getPageSize());
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), cohorts.size());
        if (start >= cohorts.size()) {
            return new PageImpl<>(Collections.emptyList(), pageRequest, cohorts.size());
        }
        return new PageImpl<>(cohorts.subList(start, end), pageRequest, cohorts.size());
    }

    @Override
    public void deleteCohort(String id) throws MeedlException {
        MeedlValidator.validateUUID(id);
        cohortOutputPort.deleteCohort(id);
    }

    @Override
    public Cohort searchForCohortInAProgram(String cohortName, String programId) throws MeedlException {
        return cohortOutputPort.searchForCohortInAProgram(cohortName,programId);
    }

    @Override
    public void inviteCohort(String userId, String programId, String cohortId) throws MeedlException {
        Cohort foundCohort = viewCohortDetails(userId,programId,cohortId);
        List<CohortLoanee> cohortLoanees = cohortLoaneeOutputPort.findAllLoaneesByCohortId(foundCohort);
        cohortLoanees
                .forEach(this::inviteTrainee);

    }
    private void inviteTrainee(CohortLoanee cohortLoanee){}
}
