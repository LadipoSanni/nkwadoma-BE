package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoaneeLoanBreakDownOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoaneeLoanBreakdownEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoaneeLoanBreakDownMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeLoanBreakDownRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoaneeLoanBreakDownPersistenceAdapter implements LoaneeLoanBreakDownOutputPort {

    private final LoaneeLoanBreakDownMapper loaneeLoanBreakDownMapper;
    private final LoaneeLoanBreakDownRepository loaneeLoanBreakDownRepository;


    @Override
    public List<LoaneeLoanBreakdown> saveAll(List<LoaneeLoanBreakdown> loaneeLoanBreakdowns,Loanee loanee) throws MeedlException {
        for (LoaneeLoanBreakdown loanBreakdown : loaneeLoanBreakdowns){
            loanBreakdown.validate();
            MeedlValidator.validateObjectInstance(loanBreakdown);
            loanBreakdown.setLoanee(loanee);
        }
        List<LoaneeLoanBreakdownEntity> loanBreakdownEntities =
                loaneeLoanBreakDownMapper.toLoaneeLoanBreakdownEntities(loaneeLoanBreakdowns);
        loanBreakdownEntities = loaneeLoanBreakDownRepository.saveAll(loanBreakdownEntities);
        log.info("Saved all loanee loan break down for loanee : {}", loanee.getUserIdentity().getEmail());
        return loaneeLoanBreakDownMapper.toLoaneeLoanBreakdown(loanBreakdownEntities);
    }

    @Override
    public void deleteAll(List<LoaneeLoanBreakdown> loaneeLoanBreakdowns) throws MeedlException {
        for (LoaneeLoanBreakdown loanBreakdown : loaneeLoanBreakdowns){
            MeedlValidator.validateObjectInstance(loanBreakdown);
        }
        List<LoaneeLoanBreakdownEntity> loanBreakdownEntities =
                loaneeLoanBreakDownMapper.toLoaneeLoanBreakdownEntities(loaneeLoanBreakdowns);
        loaneeLoanBreakDownRepository.deleteAll(loanBreakdownEntities);
    }

    @Override
    public List<LoaneeLoanBreakdown> findAllByLoaneeId(String loaneeId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeId);
        List<LoaneeLoanBreakdownEntity> loanBreakdownEntities =
                loaneeLoanBreakDownRepository.findAllByLoaneeId(loaneeId);
        return loaneeLoanBreakDownMapper.toLoaneeLoanBreakdown(loanBreakdownEntities);
    }
}
