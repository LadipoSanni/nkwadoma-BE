package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.MonthlyInterest;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MonthlyInterestOutputPort {


    MonthlyInterest save(MonthlyInterest monthlyInterest) throws MeedlException;

    void deleteById(String monthlyInterestId) throws MeedlException;

    MonthlyInterest findByDateCreated(LocalDateTime dateCreated, String id) throws MeedlException;

    void deleteAllByLoaneeLoanDetailId(String loaneeLoanDetailId) throws MeedlException;
}
