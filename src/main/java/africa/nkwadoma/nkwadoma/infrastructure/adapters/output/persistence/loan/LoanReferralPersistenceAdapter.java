package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loan;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanReferralOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanReferralEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoanReferralMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanReferralRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoanReferralPersistenceAdapter implements LoanReferralOutputPort {

    private final LoanReferralRepository loanReferralRepository;
    private final LoanReferralMapper loanReferralMapper;

    @Override
    public LoanReferral createLoanReferral(Loanee loanee) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee);
        LoanReferral loanReferral = new LoanReferral();
        loanReferral.setLoanee(loanee);
        loanReferral.setLoanReferralStatus(LoanReferralStatus.PENDING);
        loanReferral.validate();
        LoanReferralEntity loanReferralEntity =
                loanReferralMapper.toLoanReferralEntity(loanReferral);
        loanReferralEntity = loanReferralRepository.save(loanReferralEntity);
        return loanReferralMapper.toLoanReferral(loanReferralEntity);
    }
}
