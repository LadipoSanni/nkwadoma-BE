package africa.nkwadoma.nkwadoma.application.ports.output.bankdetail;

import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;

public interface EntityBankDetailOutputPort {
    BankDetail save(BankDetail bankDetail);
}
