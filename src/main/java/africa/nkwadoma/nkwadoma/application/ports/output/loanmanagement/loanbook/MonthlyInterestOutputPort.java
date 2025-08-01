package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.MonthlyInterest;

public interface MonthlyInterestOutputPort {


    MonthlyInterest save(MonthlyInterest monthlyInterest) throws MeedlException;

    void deleteById(String monthlyInterestId) throws MeedlException;
}
