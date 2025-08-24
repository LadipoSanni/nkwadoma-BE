package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.bankdetail;

import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.bankdetail.BankDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BankDetailMapper {
    @Mapping(target = "bankName", source = "bankName")
    @Mapping(target = "bankNumber", source = "bankNumber")
    BankDetailEntity toBankDetailEntity(BankDetail bankDetail);

    @Mapping(target = "bankName", source = "bankName")
    @Mapping(target = "bankNumber", source = "bankNumber")
    BankDetail toBankDetail(BankDetailEntity bankDetailEntity);

    List<BankDetail> map(List<BankDetailEntity> bankDetailEntities);
}
