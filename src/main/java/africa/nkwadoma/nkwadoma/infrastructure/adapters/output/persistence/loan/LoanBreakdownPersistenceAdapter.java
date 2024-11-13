package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loan;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanBreakdownEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoanBreakdownMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanBreakdownRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RequiredArgsConstructor
public class LoanBreakdownPersistenceAdapter implements LoanBreakdownOutputPort {

    private final LoanBreakdownRepository loanBreakdownRepository;
    private final LoanBreakdownMapper loanBreakdownMapper;


    @Override
    public List<LoanBreakdown> findAllByCohortId(String id) {
        List<LoanBreakdownEntity> loanBreakdownEntities =
                loanBreakdownRepository.findAllByCohortId(id);
        return loanBreakdownMapper.toLoanBreakdownList(loanBreakdownEntities);
    }

    @Override
    public List<LoanBreakdown> saveAll(List<LoanBreakdown> loanBreakdown) {
        List<LoanBreakdownEntity> loanBreakdownEntities =
                loanBreakdownMapper.toLoanBreakdownEntityList(loanBreakdown);
        loanBreakdownEntities = loanBreakdownRepository.saveAll(loanBreakdownEntities);
        return loanBreakdownMapper.toLoanBreakdownList(loanBreakdownEntities);
    }


}
