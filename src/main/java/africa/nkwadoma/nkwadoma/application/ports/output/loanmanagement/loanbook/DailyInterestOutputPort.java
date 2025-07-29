package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.DailyInterest;

public interface DailyInterestOutputPort {

    DailyInterest save(DailyInterest dailyInterest) throws MeedlException;
}
