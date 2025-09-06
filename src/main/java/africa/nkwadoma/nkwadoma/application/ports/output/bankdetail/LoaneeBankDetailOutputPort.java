package africa.nkwadoma.nkwadoma.application.ports.output.bankdetail;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.LoaneeBankDetail;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;

import java.util.List;

public interface LoaneeBankDetailOutputPort {
    LoaneeBankDetail save(LoaneeBankDetail loaneeBankDetail) throws MeedlException;

    List<BankDetail> findAllBankDetailOfLoanee(Loanee loanee) throws MeedlException;

    void deleteById(String loaneeBankDetailId) throws MeedlException;

    LoaneeBankDetail findApprovedBankDetailByLoaneeId(Loanee loanee) throws MeedlException;
}
