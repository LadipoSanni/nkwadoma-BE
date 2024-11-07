package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;

import java.util.*;

@RequiredArgsConstructor
public class CohortService implements CohortUseCase {

    private final CohortOutputPort cohortOutputPort;

    @Override
    public Cohort createCohort(Cohort cohort) throws MeedlException {
        return cohortOutputPort.saveCohort(cohort);
    }

    @Override
    public Cohort viewCohortDetails(String userId, String programId, String cohortId) throws MeedlException {
        return cohortOutputPort.viewCohortDetails(userId,programId,cohortId);
    }



    @Override
    public Page<Cohort> viewAllCohortInAProgram(String id, int pageSize, int pageNumber) throws MeedlException {
        List<Cohort> cohorts = cohortOutputPort.findAllCohortInAProgram(id);
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), cohorts.size());
        if (start >= cohorts.size()) {
            return new PageImpl<>(Collections.emptyList(), pageRequest, cohorts.size());
        }
        return new PageImpl<>(cohorts.subList(start, end), pageRequest, cohorts.size());
    }
}
