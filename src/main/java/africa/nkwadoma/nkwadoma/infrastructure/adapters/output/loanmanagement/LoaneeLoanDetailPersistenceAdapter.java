package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoaneeLoanDetailEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoaneeLoanDetailMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeLoanDetailRepository;
import lombok.RequiredArgsConstructor;

import java.util.*;

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

    @Override
    public void delete(String LoaneeLoanDetailId) throws MeedlException {
        MeedlValidator.validateUUID(LoaneeLoanDetailId, "Please provide a valid Loanee LoanDetailId");
        Optional<LoaneeLoanDetailEntity> loaneeLoanDetailEntity = loaneeLoanDetailRepository.findById(LoaneeLoanDetailId);
        loaneeLoanDetailEntity.ifPresent(loaneeLoanDetailRepository::delete);
    }
}
