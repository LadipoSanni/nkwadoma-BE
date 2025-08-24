package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.bankdetail;

import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.EntityBankDetailOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.bankdetail.EntityBankDetailMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.bankdetail.EntityBankDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.bankdetail.EntityBankDetailRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@AllArgsConstructor
public class EntityBankDetailAdapter implements EntityBankDetailOutputPort {
    private final EntityBankDetailMapper entityBankDetailMapper;
    private final EntityBankDetailRepository entityBankDetailRepository;

    @Override
    public BankDetail save(BankDetail bankDetail){
        EntityBankDetail entityBankDetail = entityBankDetailMapper.map(bankDetail);
        entityBankDetail = entityBankDetailRepository.save(entityBankDetail);
        return entityBankDetailMapper.map(entityBankDetail);
    }
}
