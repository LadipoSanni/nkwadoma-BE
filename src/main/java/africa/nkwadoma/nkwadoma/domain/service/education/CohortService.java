package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import lombok.*;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.*;

import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages.*;

@RequiredArgsConstructor
public class CohortService implements CohortUseCase {

    private final CohortOutputPort cohortOutputPort;

    private final ProgramOutputPort programOutputPort;
    private final LoaneeOutputPort loaneeOutputPort;

    @Override
    public Cohort createOrEditCohort(Cohort cohort) throws MeedlException {
        return cohortOutputPort.saveCohort(cohort);
    }

    @Override
    public Cohort viewCohortDetails(String userId, String programId, String cohortId) throws MeedlException {
        return cohortOutputPort.viewCohortDetails(userId, programId, cohortId);
    }

    @Override
    public Page<Cohort> viewAllCohortInAProgram(String programId, int pageNumber, int pageSize) throws MeedlException {
        MeedlValidator.validateUUID(programId);
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validatePageSize(pageSize);
        Program foundProgram = programOutputPort.findProgramById(programId);
        if (ObjectUtils.isEmpty(foundProgram)) {
            throw new MeedlException(PROGRAM_NOT_FOUND.getMessage());
        }
        List<Cohort> cohorts = cohortOutputPort.findAllCohortInAProgram(programId);
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
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
        return cohortOutputPort.searchForCohortInAProgram(cohortName, programId);
    }

    @Override
    public void inviteCohort(String userId, String programId, String cohortId) throws MeedlException {
        Cohort foundCohort = viewCohortDetails(userId, programId, cohortId);
        List<Loanee> cohortLoanees = loaneeOutputPort.findAllLoaneesByCohortId(foundCohort);
        cohortLoanees
                .forEach(this::inviteTrainee);

    }

    private void inviteTrainee(Loanee loanee) {
    }
}
