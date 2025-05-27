package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.loanBook.RepaymentHistoryOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.loanBook.RepaymentHistoryMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.RepaymentHistoryEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.loanBook.RepaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RepaymentHistoryAdapter implements RepaymentHistoryOutputPort {
    private final RepaymentHistoryMapper repaymentHistoryMapper;
    private final RepaymentHistoryRepository repaymentHistoryRepository;

    @Override
    public RepaymentHistory save(RepaymentHistory repaymentHistory){
        repaymentHistory.validate();
        RepaymentHistoryEntity repaymentHistoryEntity = repaymentHistoryMapper.map(repaymentHistory);
        RepaymentHistoryEntity savedRepaymentHistoryEntity = repaymentHistoryRepository.save(repaymentHistoryEntity);
        return repaymentHistoryMapper.map(savedRepaymentHistoryEntity);
    }
}
