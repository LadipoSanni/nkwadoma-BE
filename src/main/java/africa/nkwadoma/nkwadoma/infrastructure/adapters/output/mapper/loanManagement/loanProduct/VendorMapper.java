package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.loanProduct;

import africa.nkwadoma.nkwadoma.domain.model.loan.Vendor;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.VendorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VendorMapper {
    VendorEntity map(Vendor vendor);
    Vendor map(VendorEntity vendor);
}
