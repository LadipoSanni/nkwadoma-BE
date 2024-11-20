package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class LoanRequestAdapter implements LoanRequestOutputPort {
    private final LoanRequestRepository loanRequestRepository;
    private final LoanRequestMapper loanRequestMapper;

    @Override
    public LoanRequest save(LoanRequest loanRequest) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanRequest);
        loanRequest.validate();
        LoanRequestEntity loanRequestEntity = loanRequestMapper.toLoanRequestEntity(loanRequest);
        LoanRequestEntity savedLoanRequestEntity = loanRequestRepository.save(loanRequestEntity);
        return loanRequestMapper.toLoanRequest(savedLoanRequestEntity);
    }

    @Override
    public Page<LoanRequest> viewAll(int pageNumber, int pageSize) throws MeedlException {
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validatePageSize(pageSize);
        Page<LoanRequestEntity> loanRequestEntities = loanRequestRepository.findAll(
                PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc("dateTimeApproved")))
        );
        return loanRequestEntities.map(loanRequestMapper::toLoanRequest);
    }
}
