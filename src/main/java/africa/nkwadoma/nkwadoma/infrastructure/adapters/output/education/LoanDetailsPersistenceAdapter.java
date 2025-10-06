package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.LoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.LoanDetailEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.LoanDetailMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.LoanDetailRepository;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class LoanDetailsPersistenceAdapter implements LoanDetailsOutputPort {

    private final LoanDetailRepository loanDetailRepository;
    private final LoanDetailMapper loanDetailMapper;

    @Override
    public LoanDetail saveLoanDetails(LoanDetail loanDetail) throws MeedlException {
        loanDetail.validate();
        LoanDetailEntity loanDetailEntity =
                loanDetailMapper.toLoanDetailEntity(loanDetail);
        loanDetailRepository.save(loanDetailEntity);
        return loanDetailMapper.toLoanDetail(loanDetailEntity);
    }
}
