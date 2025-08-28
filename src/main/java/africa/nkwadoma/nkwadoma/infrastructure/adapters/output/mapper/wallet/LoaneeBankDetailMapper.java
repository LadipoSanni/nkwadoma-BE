package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.wallet;

import africa.nkwadoma.nkwadoma.domain.model.bankdetail.LoaneeBankDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.wallet.LoaneeBankDetailEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoaneeBankDetailMapper {

    @Mapping(target = "loaneeEntity", source = "loanee")
    @Mapping(target = "bankDetailEntity", source = "bankDetail")
    LoaneeBankDetailEntity map(LoaneeBankDetail loaneeBankDetail);

    @InheritInverseConfiguration
    LoaneeBankDetail map(LoaneeBankDetailEntity loaneeBankDetailEntity);
}
