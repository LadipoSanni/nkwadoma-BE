package africa.nkwadoma.nkwadoma.domain.service.bankDetail;

import africa.nkwadoma.nkwadoma.application.ports.input.walletManagement.BankDetailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.BankDetailOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.BankDetailMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankDetailService implements BankDetailUseCase {
    private final BankDetailOutputPort bankDetailOutputPort;

    @Override
    public BankDetail addBankDetails(BankDetail bankDetail) throws MeedlException {
        MeedlValidator.validateObjectInstance(bankDetail, BankDetailMessages.INVALID_BANK_DETAIL.getMessage());
        bankDetail.validate();
        bankDetail = bankDetailOutputPort.save(bankDetail);
        bankDetail.setResponse("Added bank details successfully");
        return bankDetail;
    }
}
