package africa.nkwadoma.nkwadoma.application.ports.output.bankDetail;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankDetail.BankDetail;

public interface BankDetailOutputPort {
    BankDetail save(BankDetail bankDetail) throws MeedlException;
}
