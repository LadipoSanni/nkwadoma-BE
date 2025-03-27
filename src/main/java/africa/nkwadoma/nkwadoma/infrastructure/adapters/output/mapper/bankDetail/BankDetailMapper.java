package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.bankDetail;

import africa.nkwadoma.nkwadoma.domain.model.bankDetail.BankDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.bankDetail.BankDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BankDetailMapper {
    @Mapping(target = "bankName", source = "bankName")
    @Mapping(target = "bankNumber", source = "bankNumber")
    BankDetailEntity toBankDetailEntity(BankDetail bankDetail);

    @Mapping(target = "bankName", source = "bankName")
    @Mapping(target = "bankNumber", source = "bankNumber")
    BankDetail toBankDetail(BankDetailEntity bankDetailEntity);
}
