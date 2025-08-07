package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanAggregateOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanAggregate;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeLoanAggregateEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoaneeLoanAggregateMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeLoanAggregateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoaneeLoanAggregateAdapter implements LoaneeLoanAggregateOutputPort {

    private final LoaneeLoanAggregateMapper loaneeLoanAggregateMapper;
    private final LoaneeLoanAggregateRepository loaneeLoanAggregateRepository;

    @Override
    public LoaneeLoanAggregate save(LoaneeLoanAggregate loaneeLoanAggregate) throws MeedlException {
        MeedlValidator.validateObjectInstance(loaneeLoanAggregate,"Loanee loan aggregate cannot be empty");
        loaneeLoanAggregate.validate();
        LoaneeLoanAggregateEntity loaneeLoanAggregateEntity = loaneeLoanAggregateMapper.toLoaneeLoanAggregateEntity(loaneeLoanAggregate);
        loaneeLoanAggregateEntity = loaneeLoanAggregateRepository.save(loaneeLoanAggregateEntity);
        return loaneeLoanAggregateMapper.toLoaneeLoanAggregate(loaneeLoanAggregateEntity);
    }

    @Override
    public void delete(String loaneeLoanAggregateId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeLoanAggregateId,"Loanee loan aggregate id cannot be empty");
        loaneeLoanAggregateRepository.deleteById(loaneeLoanAggregateId);
    }

    @Override
    public LoaneeLoanAggregate findByLoaneeId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id,"Loanee id cannot be empty");
        LoaneeLoanAggregateEntity loaneeLoanAggregateEntity =
                loaneeLoanAggregateRepository.findByLoaneeId(id);
        return loaneeLoanAggregateMapper.toLoaneeLoanAggregate(loaneeLoanAggregateEntity);
    }
}
