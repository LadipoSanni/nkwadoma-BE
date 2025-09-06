package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.wallet;

import africa.nkwadoma.nkwadoma.domain.model.bankdetail.OrganizationBankDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.wallet.OrganizationBankDetailEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrganizationBankDetailMapper {
    @Mapping(target = "organizationEntity", source = "organizationIdentity")
    @Mapping(target = "bankDetailEntity", source = "bankDetail")
    OrganizationBankDetailEntity map(OrganizationBankDetail organizationBankDetail);

    @InheritInverseConfiguration
    OrganizationBankDetail map(OrganizationBankDetailEntity organizationBankDetailEntity);
}
