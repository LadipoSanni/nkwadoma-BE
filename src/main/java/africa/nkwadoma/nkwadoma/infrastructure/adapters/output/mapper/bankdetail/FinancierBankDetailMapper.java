package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.bankdetail;

import africa.nkwadoma.nkwadoma.domain.model.bankdetail.FinancierBankDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.bankdetail.FinancierBankDetailEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinancierBankDetailMapper {
    @Mapping(target = "financierEntity", source = "financier")
    @Mapping(target = "bankDetailEntity", source = "bankDetail")
    FinancierBankDetailEntity map(FinancierBankDetail financierBankDetail);

    @InheritInverseConfiguration
    FinancierBankDetail map(FinancierBankDetailEntity financierBankDetailEntity);
}
