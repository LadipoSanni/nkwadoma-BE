package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanBreakDownOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeLoanBreakdownEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoaneeLoanBreakDownMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeLoanBreakDownRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoaneeLoanBreakDownPersistenceAdapter implements LoaneeLoanBreakDownOutputPort {

    private final LoaneeLoanBreakDownMapper loaneeLoanBreakDownMapper;
    private final LoaneeLoanBreakDownRepository loaneeLoanBreakDownRepository;


    @Override
    public List<LoaneeLoanBreakdown> saveAll(List<LoaneeLoanBreakdown> loaneeLoanBreakdowns, CohortLoanee cohortLoanee) throws MeedlException {
        for (LoaneeLoanBreakdown loanBreakdown : loaneeLoanBreakdowns){
            loanBreakdown.validate();
            MeedlValidator.validateObjectInstance(loanBreakdown, LoaneeMessages.LOANEE_CANNOT_BE_EMPTY.getMessage());
            loanBreakdown.setCohortLoanee(cohortLoanee);
        }
        List<LoaneeLoanBreakdownEntity> loanBreakdownEntities =
                loaneeLoanBreakDownMapper.toLoaneeLoanBreakdownEntities(loaneeLoanBreakdowns);
        loanBreakdownEntities = loaneeLoanBreakDownRepository.saveAll(loanBreakdownEntities);
        log.info("Saved all loanee loan break down for loanee : {}", cohortLoanee.getLoanee().getUserIdentity().getEmail());
        return loaneeLoanBreakDownMapper.toLoaneeLoanBreakdown(loanBreakdownEntities);
    }

    @Override
    public void deleteAll(List<LoaneeLoanBreakdown> loaneeLoanBreakdowns) throws MeedlException {
        for (LoaneeLoanBreakdown loanBreakdown : loaneeLoanBreakdowns){
            log.info("loanbreak down about to be deleted {}",loanBreakdown);
            MeedlValidator.validateObjectInstance(loanBreakdown, LoaneeMessages.LOANEE_CANNOT_BE_EMPTY.getMessage());
        }
        List<LoaneeLoanBreakdownEntity> loanBreakdownEntities =
                loaneeLoanBreakDownMapper.toLoaneeLoanBreakdownEntities(loaneeLoanBreakdowns);
        loaneeLoanBreakDownRepository.deleteAll(loanBreakdownEntities);
    }

    @Override
    public List<LoaneeLoanBreakdown> findAllLoaneeLoanBreakDownByCohortLoaneeId(String cohortLoaneeId) throws MeedlException {
        MeedlValidator.validateUUID(cohortLoaneeId, CohortMessages.COHORT_LOANEE_ID_CANNOT_BE_EMPTY.getMessage());
        List<LoaneeLoanBreakdownEntity> loanBreakdownEntities =
                loaneeLoanBreakDownRepository.findAllByCohortLoaneeId(cohortLoaneeId);
        log.info("Found loanee loan breakdown = = {} ", loanBreakdownEntities.size());
        return loaneeLoanBreakDownMapper.toLoaneeLoanBreakdown(loanBreakdownEntities);
    }

    @Override
    public LoaneeLoanBreakdown findById(String loaneeLoanBreakdownId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeLoanBreakdownId,"Loanee loan breakdown id cannot be empty");
        LoaneeLoanBreakdownEntity  loaneeLoanBreakdown =
                loaneeLoanBreakDownRepository.findById(loaneeLoanBreakdownId).orElse(null);
        return loaneeLoanBreakDownMapper.toLoanBreakdown(loaneeLoanBreakdown);
    }

    @Transactional
    @Override
    public void deleteByCohortLoaneeid(String id) {
        loaneeLoanBreakDownRepository.deleteAllByCohortLoaneeId(id);
    }
}
