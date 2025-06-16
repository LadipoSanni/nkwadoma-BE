package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.bankdetail;

import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.BankDetailOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.bankdetail.BankDetailMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.bankDetail.BankDetailEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.bankdetail.BankDetailRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class BankDetailAdapter implements BankDetailOutputPort {
    private final BankDetailRepository bankDetailRepository;
    private final BankDetailMapper bankDetailMapper;

    @Override
    public BankDetail save(BankDetail bankDetail) throws MeedlException {
        MeedlValidator.validateObjectInstance(bankDetail, "Bank Detail must be provided.");
        log.info("Bank Detail before saving : {}", bankDetail);
        bankDetail.validate();
        BankDetailEntity bankDetailEntity = bankDetailMapper.toBankDetailEntity(bankDetail);
        log.info("Bank Detail Mapped to entity : {}", bankDetailEntity);
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
