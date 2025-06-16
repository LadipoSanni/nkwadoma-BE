package africa.nkwadoma.nkwadoma.application.ports.output.bankdetail;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;

public interface BankDetailOutputPort {
    BankDetail save(BankDetail bankDetail) throws MeedlException;

    void deleteById(String id) throws MeedlException;

    BankDetail findByBankDetailId(String bankDetailId) throws MeedlException;
}
