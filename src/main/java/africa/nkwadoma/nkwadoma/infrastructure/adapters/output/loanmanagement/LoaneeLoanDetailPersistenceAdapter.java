package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeLoanDetailEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoaneeLoanDetailMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanSummaryProjection;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeLoanDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
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

    @Override
    public LoaneeLoanDetail findByCohortLoaneeId(String cohortLoaneeId) throws MeedlException {
        MeedlValidator.validateUUID(cohortLoaneeId, CohortMessages.COHORT_LOANEE_ID_CANNOT_BE_EMPTY.getMessage());
        LoaneeLoanDetailEntity loaneeLoanDetailEntity = loaneeLoanDetailRepository.findByCohortLoaneeId(cohortLoaneeId);
        log.info("Found loaneeLoanDetailEntity: {}", loaneeLoanDetailEntity);
        return loaneeLoanDetailMapper.toLoaneeLoanDetails(loaneeLoanDetailEntity);
    }

    @Override
    public LoanSummaryProjection getLoanSummary(String userId) throws MeedlException {
        MeedlValidator.validateUUID(userId, UserMessages.INVALID_USER_ID.getMessage());
        return loaneeLoanDetailRepository.getLoanSummary(userId);
    }

    @Override
    public LoaneeLoanDetail findByCohortAndLoaneeId(String cohortId, String loaneeId) throws MeedlException {
        MeedlValidator.validateUUID(cohortId,CohortMessages.INVALID_COHORT_ID.getMessage());
        MeedlValidator.validateUUID(loaneeId, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        LoaneeLoanDetailEntity loaneeLoanDetailEntity = loaneeLoanDetailRepository.findByCohortAndLoaneeId(cohortId,loaneeId);
        return loaneeLoanDetailMapper.toLoaneeLoanDetails(loaneeLoanDetailEntity);
    }
}
