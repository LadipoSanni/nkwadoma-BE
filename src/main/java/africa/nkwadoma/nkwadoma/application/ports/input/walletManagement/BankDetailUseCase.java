package africa.nkwadoma.nkwadoma.application.ports.input.walletManagement;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;

public interface BankDetailUseCase {

    BankDetail addBankDetails(BankDetail bankDetail) throws MeedlException;
}
