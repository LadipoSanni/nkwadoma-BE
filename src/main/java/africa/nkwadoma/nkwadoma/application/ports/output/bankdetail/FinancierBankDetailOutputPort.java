package africa.nkwadoma.nkwadoma.application.ports.output.bankdetail;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.FinancierBankDetail;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;

import java.util.List;

public interface FinancierBankDetailOutputPort {
    FinancierBankDetail save(FinancierBankDetail financierBankDetail) throws MeedlException;

    FinancierBankDetail findByFinancierIdAndStatus(Financier financier, ActivationStatus activationStatus) throws MeedlException;

    FinancierBankDetail findApprovedBankDetailByFinancierId(Financier financier) throws MeedlException;

    List<BankDetail> findAllBankDetailOfFinancier(Financier financier) throws MeedlException;

    void deleteById(String financierBankDetailId) throws MeedlException;
}
