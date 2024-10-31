package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

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
        return cohortOutputPort.findAllCohortInAProgram(id,pageSize,pageNumber);
    }
}
