package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.bankDetail;

import africa.nkwadoma.nkwadoma.application.ports.output.bankDetail.BankDetailOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.bankDetail.BankDetail;
import org.springframework.stereotype.Component;

@Component
public class BankDetailAdapter implements BankDetailOutputPort {

    @Override
    public BankDetail save(BankDetail bankDetail) {

    }
}
