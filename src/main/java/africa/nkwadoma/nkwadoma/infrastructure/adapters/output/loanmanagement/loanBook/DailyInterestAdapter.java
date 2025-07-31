package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement.loanBook;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.DailyInterestOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.DailyInterest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.loanBook.DailyInterestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyInterestAdapter implements DailyInterestOutputPort {

    private DailyInterestMapper dailyInterestMapper;

    @Override
    public DailyInterest save(DailyInterest dailyInterest) throws MeedlException {
        dailyInterest.validate();

        DailyInterestEntity dailyInterestEntity =
                dailyInterestMapper.toDailyInterestEntity(dailyInterest);

        return null;
    }
}
