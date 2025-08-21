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

import java.time.LocalDateTime;
import java.util.Optional;


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

    @Override
    public Optional<MonthlyInterest> findOptionalByCreatedAt(LocalDateTime localDateTime, String loaneeLoanDetailId) throws MeedlException {
        MeedlValidator.validateObjectInstance(localDateTime, "Please provide monthly interest date to find");
        log.info("finding monthly interest by date created {}", localDateTime);
        Optional<MonthlyInterestEntity> optionalMonthlyInterestEntity = monthlyInterestRepository.findByCreatedAtAndLoaneeLoanDetail_Id(localDateTime, loaneeLoanDetailId);
        return optionalMonthlyInterestEntity.map(monthlyInterestMapper::toMonthlyInterest);
    }
    public MonthlyInterest findByDateCreated(LocalDateTime dateCreated, String id) throws MeedlException {
        MeedlValidator.validateUUID(id,"Loanee loan detail id cannot be empty");
        MeedlValidator.validateObjectInstance(dateCreated,"Date created cannot be empty");
        MonthlyInterestEntity monthlyInterestEntity =
                monthlyInterestRepository.findByLoaneeLoanDetailIdAndCreatedAtMonthAndCreatedAtYear(id,dateCreated.getMonth().getValue()
                        ,dateCreated.getYear());

        return monthlyInterestMapper.toMonthlyInterest(monthlyInterestEntity);
    }

    @Override
    public void deleteAllByLoaneeLoanDetailId(String loaneeLoanDetailId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeLoanDetailId, "Provide a valid loanee loan detail id");
        monthlyInterestRepository.deleteAllByLoaneeLoanDetail_Id(loaneeLoanDetailId);
    }
}
