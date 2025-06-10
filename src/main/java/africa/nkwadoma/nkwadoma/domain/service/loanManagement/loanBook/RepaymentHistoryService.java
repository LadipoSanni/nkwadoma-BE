package africa.nkwadoma.nkwadoma.domain.service.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanManagement.loanBook.RepaymentHistoryUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.loanBook.RepaymentHistoryOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.domain.validation.LoanBookValidator;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RepaymentHistoryService implements RepaymentHistoryUseCase {

    private final CohortUseCase cohortUseCase;
    private final RepaymentHistoryOutputPort repaymentHistoryOutputPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private LoanBookValidator loanBookValidator;
    private final LoaneeOutputPort loaneeOutputPort;


    @Override
    public List<RepaymentHistory> saveCohortRepaymentHistory(LoanBook loanBook) throws MeedlException {
        MeedlValidator.validateUUID(loanBook.getActorId(), "Actor id is required.");
        MeedlValidator.validateUUID(loanBook.getCohort().getId(), "Cohort id is required.");
        MeedlValidator.validateCollection(loanBook.getRepaymentHistories(), "Please provide at least one repayment history.");
        Cohort cohort = cohortUseCase.viewCohortDetails(loanBook.getActorId(), loanBook.getCohort().getId());
        log.info("Cohort found when trying to save repayment record in service {}", cohort);
        loanBook.setCohort(cohort);

        return verifyUserByEmailAndAddCohort(loanBook);
    }

    @Override
    public Page<RepaymentHistory> findAllRepaymentHistory(RepaymentHistory repaymentHistory, int pageSize, int pageNumber) throws MeedlException {
        log.info("request that got into service, actor =  {}, pageSize = {} , pageNumber = {}",repaymentHistory.getActorId()
        , pageSize, pageNumber);
        UserIdentity userIdentity = userIdentityOutputPort.findById(repaymentHistory.getActorId());
        if (userIdentity.getRole().equals(IdentityRole.PORTFOLIO_MANAGER)){
            Page<RepaymentHistory> repaymentHistories = repaymentHistoryOutputPort.findRepaymentHistoryAttachedToALoaneeOrAll(repaymentHistory,
                    pageSize, pageNumber);
            log.info("repayment histories gotten from adapter == {}",repaymentHistories.getContent().stream().toList());
            return repaymentHistories;
        }
        Loanee loanee = loaneeOutputPort.findByUserId(userIdentity.getId()).get();
        repaymentHistory.setLoaneeId(loanee.getId());
        return repaymentHistoryOutputPort.findRepaymentHistoryAttachedToALoaneeOrAll(repaymentHistory, pageSize, pageNumber);
    }

    @Override
    public Page<RepaymentHistory> searchRepaymentHistory(RepaymentHistory repaymentHistory, int pageSize, int pageNumber) throws MeedlException {
        return repaymentHistoryOutputPort.searchRepaymemtHistoryByLoaneeName(repaymentHistory,pageSize,pageNumber);
    }

    private List<RepaymentHistory> verifyUserByEmailAndAddCohort(LoanBook loanBook) {
        log.info("Verifying loanees exist before saving their repayment records:\n {}", loanBook.getRepaymentHistories());
        return loanBook.getRepaymentHistories().stream()
                .peek(repaymentHistory -> {
                    try {
                        log.info("Email of the loanee being searched for {}", repaymentHistory.getLoanee().getUserIdentity().getEmail());
                        Loanee loanee = loaneeOutputPort.findByLoaneeEmail(repaymentHistory.getLoanee().getUserIdentity().getEmail());
                        log.info("loanee found in repayment history : {}",loanee);
                        if (loanee != null) {
                            repaymentHistory.setLoanee(loanee);
                            repaymentHistory.setCohort(loanBook.getCohort());
                            repaymentHistoryOutputPort.save(repaymentHistory);
                        }
                    } catch (MeedlException e) {
                        //TODO notify user doesn't exist on the platform.
//                        updateFailureNotification(loanBook);
                        log.error("Error in repayment service. Either saving repayment or finding loanee", e);
                        log.error("Error occurred while verifying user exist on platform. {}", repaymentHistory.getLoanee().getUserIdentity().getEmail());
                    }
                }).toList();
    }
}
