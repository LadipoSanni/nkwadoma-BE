package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CohortService implements CohortUseCase {

    private final CohortOutputPort cohortOutputPort;

    @Override
    public Cohort createOrEditCohort(Cohort cohort) throws MeedlException {
        return cohortOutputPort.saveCohort(cohort);
    }

    @Override
    public Cohort viewCohortDetails(String userId, String programId, String cohortId) throws MeedlException {
        return cohortOutputPort.viewCohortDetails(userId,programId,cohortId);
    }
    @Override
    public void deleteCohort(String id) throws MeedlException {
        MeedlValidator.validateUUID(id);
        cohortOutputPort.deleteCohort(id);
    }

    @Override
    public void inviteCohort(String userId, String programId, String cohortId) throws MeedlException {
        Cohort foundCohort = viewCohortDetails(userId,programId,cohortId);
        foundCohort.getTrainees().stream()
                .forEach(this::inviteTrainee );

    }
    private void inviteTrainee(UserIdentity userIdentity){}
}
