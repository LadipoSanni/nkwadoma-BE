package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanBreakdownEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoanBreakdownMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanBreakdownRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class LoanBreakdownPersistenceAdapter implements LoanBreakdownOutputPort {
    private final LoanBreakdownRepository loanBreakdownRepository;
    private final LoanBreakdownMapper loanBreakdownMapper;


    @Override
    public List<LoanBreakdown> findAllByCohortId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id);
        List<LoanBreakdownEntity> loanBreakdownEntities =
                loanBreakdownRepository.findAllByCohortId(id);
        return loanBreakdownMapper.toLoanBreakdownList(loanBreakdownEntities);
    }

    @Override
    public List<LoanBreakdown> saveAll(List<LoanBreakdown> loanBreakdown, LoaneeLoanDetail loaneeLoanDetail) {
        List<LoanBreakdownEntity> loanBreakdownEntities =
                loanBreakdownMapper.toLoanBreakdownEntityList(loanBreakdown);
        loanBreakdownEntities = loanBreakdownRepository.saveAll(loanBreakdownEntities);
        return loanBreakdownMapper.toLoanBreakdownList(loanBreakdownEntities);
    }

    @Override
    public void deleteAll(List<LoanBreakdown> loanBreakdownList) {
        List<LoanBreakdownEntity> loanBreakdownEntities =
                loanBreakdownMapper.toLoanBreakdownEntityList(loanBreakdownList);
        loanBreakdownRepository.deleteAll(loanBreakdownEntities);
    }

    @Override
    public List<LoanBreakdown> saveAllLoanBreakDown(List<LoanBreakdown> loanBreakdown) {
        List<LoanBreakdownEntity> loanBreakdownEntities =
                loanBreakdownMapper.toLoanBreakdownEntityList(loanBreakdown);
        loanBreakdownEntities = loanBreakdownRepository.saveAll(loanBreakdownEntities);
        return loanBreakdownMapper.toLoanBreakdownList(loanBreakdownEntities);
    }



}
