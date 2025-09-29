package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanBreakdownEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.LoanBreakdownMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanBreakdownRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
public class LoanBreakdownPersistenceAdapter implements LoanBreakdownOutputPort {
    private final LoanBreakdownRepository loanBreakdownRepository;
    private final LoanBreakdownMapper loanBreakdownMapper;


    @Override
    public List<LoanBreakdown> findAllByCohortId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, CohortMessages.INVALID_COHORT_ID.getMessage());
        List<LoanBreakdownEntity> loanBreakdownEntities =
                loanBreakdownRepository.findAllByCohortId(id);
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

    @Transactional
    @Override
    public void deleteAllBreakDownAssociateWithProgram(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        loanBreakdownRepository.deleteAllLoanBreakdownByProgramId(id);
    }

    @Override
    public LoanBreakdown findByItemNameAndCohortId(String itemName,String cohortId) throws MeedlException {
        MeedlValidator.validateObjectInstance(itemName,"Item name cannot be empty");
        MeedlValidator.validateUUID(cohortId,"Cohort id cannot be empty");
        LoanBreakdownEntity loanBreakdownEntity = loanBreakdownRepository.findByItemNameIgnoreCaseAndCohortId(itemName,cohortId);
        return loanBreakdownMapper.toLoanBreakDown(loanBreakdownEntity);
    }


}
