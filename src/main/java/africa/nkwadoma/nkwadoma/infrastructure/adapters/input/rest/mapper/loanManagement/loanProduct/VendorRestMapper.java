package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement.loanProduct;

import africa.nkwadoma.nkwadoma.domain.model.loan.Vendor;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanProduct.VendorResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VendorRestMapper {
    VendorResponse map(Vendor vendor);
}
