package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement.loanBook;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.MonthlyInterestOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.MonthlyInterest;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.loanBook.MonthlyInterestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.MonthlyInterestEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.loanBook.MonthlyInterestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class MonthlyInterestAdapter implements MonthlyInterestOutputPort {

    private final MonthlyInterestMapper monthlyInterestMapper;
    private final MonthlyInterestRepository monthlyInterestRepository;


    @Override
    public MonthlyInterest save(MonthlyInterest monthlyInterest) throws MeedlException {
        MeedlValidator.validateObjectInstance(monthlyInterest,"Monthly interest cannot be empty");
        monthlyInterest.validate();

        MonthlyInterestEntity monthlyInterestEntity = monthlyInterestMapper.toMonthlyInterestEntity(monthlyInterest);
        monthlyInterestEntity = monthlyInterestRepository.save(monthlyInterestEntity);

        return monthlyInterestMapper.toMonthlyInterest(monthlyInterestEntity);
    }

    @Override
    public void deleteById(String monthlyInterestId) throws MeedlException {
        MeedlValidator.validateUUID(monthlyInterestId,"Monthly interest id cannot be empty");
        monthlyInterestRepository.deleteById(monthlyInterestId);
    }
}
