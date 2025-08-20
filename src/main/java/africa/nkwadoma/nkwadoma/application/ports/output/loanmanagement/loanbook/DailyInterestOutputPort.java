package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.DailyInterest;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

public interface DailyInterestOutputPort {

    DailyInterest save(DailyInterest dailyInterest) throws MeedlException;

    void deleteById(String dailyInterestId) throws MeedlException;

    List<DailyInterest> findAllInterestForAMonth(Month month, int year, String id) throws MeedlException;

    DailyInterest findDailyInterestForDate(LocalDateTime dateCreated,String loaneeLoanDetailId) throws MeedlException;
}
