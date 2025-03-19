package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.bankDetail;

import africa.nkwadoma.nkwadoma.application.ports.output.bankDetail.BankDetailOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankDetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.bankDetail.BankDetailMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.bankDetail.BankDetailEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.bankDetail.BankDetailRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BankDetailAdapter implements BankDetailOutputPort {
    private final BankDetailRepository bankDetailRepository;
    private final BankDetailMapper bankDetailMapper;

    @Override
    public BankDetail save(BankDetail bankDetail) throws MeedlException {
        MeedlValidator.validateObjectInstance(bankDetail, "Bank Detail must be provided.");
        bankDetail.validate();
        BankDetailEntity bankDetailEntity = bankDetailMapper.toBankDetailEntity(bankDetail);
        bankDetailEntity = bankDetailRepository.save(bankDetailEntity);
        return bankDetailMapper.toBankDetail(bankDetailEntity);
    }

    @Override
    public void deleteById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, "Bank Detail id must be provided.");
        bankDetailRepository.deleteById(id);
    }

    @Override
    public BankDetail findByBankDetailId(String bankDetailId) throws MeedlException {
        MeedlValidator.validateUUID(bankDetailId, "Bank Detail must be provided.");
        BankDetailEntity bankDetailEntity = bankDetailRepository.findById(bankDetailId)
                .orElseThrow(() -> new MeedlException("Bank Detail not found."));
        return bankDetailMapper.toBankDetail(bankDetailEntity);
    }
}
