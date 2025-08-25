package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.bankdetail;

import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.bankdetail.EntityBankDetail;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EntityBankDetailMapper {
    @Mapping(target = "entityId", source = "bankDetail.entityId")
    @Mapping(target = "bankDetail", source = "bankDetail")
    EntityBankDetail map(BankDetail bankDetail);

    @Mapping(target = "entityBankDetailId", source = "id")
//    @Mapping(target = "entityBankDetailId", source = "id")
    BankDetail map(EntityBankDetail entityBankDetail);
}
