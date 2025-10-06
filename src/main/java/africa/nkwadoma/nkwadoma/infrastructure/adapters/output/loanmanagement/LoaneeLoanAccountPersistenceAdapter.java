package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanAccountOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanAccount;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.LoaneeLoanAccountMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeLoanAccountEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeLoanAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoaneeLoanAccountPersistenceAdapter implements LoaneeLoanAccountOutputPort {


    private final LoaneeLoanAccountRepository loaneeLoanAccountRepository;
    private final LoaneeLoanAccountMapper loaneeLoanAccountMapper;

    @Override
    public LoaneeLoanAccount save(LoaneeLoanAccount loaneeLoanAccount) throws MeedlException {
        loaneeLoanAccount.validate();
        LoaneeLoanAccountEntity loaneeLoanAccountEntity = loaneeLoanAccountMapper.toLoaneeLoanAccountEntity(loaneeLoanAccount);
        loaneeLoanAccountEntity = loaneeLoanAccountRepository.save(loaneeLoanAccountEntity);
        return loaneeLoanAccountMapper.toLoaneeLoanAccount(loaneeLoanAccountEntity);
    }

    @Override
    public void deleteLoaneeLoanAccount(String loaneeLoanAccountId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeLoanAccountId, "Please provide a valid loanee loan account id");
        loaneeLoanAccountRepository.deleteById(loaneeLoanAccountId);
    }

    @Override
    public LoaneeLoanAccount findByLoaneeId(String loaneeId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeId, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        LoaneeLoanAccountEntity loaneeLoanAccountEntity = loaneeLoanAccountRepository.findByLoaneeId(loaneeId);
        return loaneeLoanAccountMapper.toLoaneeLoanAccount(loaneeLoanAccountEntity);
    }
}
