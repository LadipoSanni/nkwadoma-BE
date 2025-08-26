package africa.nkwadoma.nkwadoma.application.ports.output.bankdetail;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.FinancierBankDetail;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;

public interface FinancierBankDetailOutputPort {
    FinancierBankDetail save(FinancierBankDetail financierBankDetail) throws MeedlException;

    FinancierBankDetail findByFinancierIdAndStatus(Financier financier, ActivationStatus activationStatus) throws MeedlException;

    FinancierBankDetail findApprovedBankDetailByFinancierId(Financier financier) throws MeedlException;
}
