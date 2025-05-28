package africa.nkwadoma.nkwadoma.domain.service.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanManagement.loanBook.RepaymentHistoryUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.loanBook.RepaymentHistoryOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RepaymentHistoryService implements RepaymentHistoryUseCase {

    private final CohortUseCase cohortUseCase;
    private final RepaymentHistoryOutputPort repaymentHistoryOutputPort;

    @Override
    public List<RepaymentHistory> saveCohortRepaymentHistory(List<RepaymentHistory> repaymentHistories, String actorId, String cohortId) throws MeedlException {
        MeedlValidator.validateUUID(actorId, "Actor id is required.");
        MeedlValidator.validateUUID(cohortId, "Cohort id is required.");
        MeedlValidator.validateCollection(repaymentHistories, "Please provide at least one repayment history.");
        Cohort cohort = cohortUseCase.viewCohortDetails(actorId, cohortId);
        log.info("Cohort found when trying to save repayment record in service {}", cohort);
        verifyLoaneesExist(repaymentHistories);
        return repaymentHistories.stream()
                .peek(repaymentHistory -> repaymentHistory.setCohort(cohort))
                .map(repaymentHistoryOutputPort::save)
                .toList();
    }

    private void verifyLoaneesExist(List<RepaymentHistory> repaymentHistories) {
        log.info("Verifying loanees exist before saving their repayment records:\n {}", repaymentHistories);
    }
}
