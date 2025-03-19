package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.bankDetail;

import africa.nkwadoma.nkwadoma.domain.model.bankDetail.BankDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.bankDetail.BankDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BankDetailMapper {
    BankDetailEntity toBankDetailEntity(BankDetail bankDetail);

    BankDetail toBankDetail(BankDetailEntity bankDetailEntity);
}
