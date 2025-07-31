package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement.loanBook;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.DailyInterestOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.DailyInterest;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.loanBook.DailyInterestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.DailyInterestEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.loanBook.DailyInterestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyInterestAdapter implements DailyInterestOutputPort {

    private final DailyInterestMapper dailyInterestMapper;
    private final DailyInterestRepository dailyInterestRepository;

    @Override
    public DailyInterest save(DailyInterest dailyInterest) throws MeedlException {
        MeedlValidator.validateObjectInstance(dailyInterest,"Daily Interest cannot be empty");
        dailyInterest.validate();

        DailyInterestEntity dailyInterestEntity =
                dailyInterestMapper.toDailyInterestEntity(dailyInterest);
        dailyInterestEntity = dailyInterestRepository.save(dailyInterestEntity);
        return dailyInterestMapper.toDailyInterest(dailyInterestEntity);
    }

    @Override
    public void deleteById(String dailyInterestId) throws MeedlException {
        MeedlValidator.validateUUID(dailyInterestId,"Daily interest id cannot be empty");
        dailyInterestRepository.deleteById(dailyInterestId);
    }
}
