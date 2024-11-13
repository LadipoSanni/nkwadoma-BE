package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoaneeLoanDetailEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoaneeLoanDetailMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeLoanDetailRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoaneeLoanDetailPersistenceAdapter implements LoaneeLoanDetailsOutputPort {

    private final LoaneeLoanDetailRepository loaneeLoanDetailRepository;
    private final LoaneeLoanDetailMapper loaneeLoanDetailMapper;



    @Override
    public LoaneeLoanDetail save(LoaneeLoanDetail loaneeLoanDetail) {
        LoaneeLoanDetailEntity loanDetailEntity =
                loaneeLoanDetailMapper.toLoaneeLoanDetailsEnitity(loaneeLoanDetail);
        loanDetailEntity = loaneeLoanDetailRepository.save(loanDetailEntity);
        return loaneeLoanDetailMapper.toLoaneeLoanDetails(loanDetailEntity);
    }
}
