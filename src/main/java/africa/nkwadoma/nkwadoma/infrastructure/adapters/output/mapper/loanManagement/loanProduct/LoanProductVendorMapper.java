package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.loanProduct;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProductVendor;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanProductVendorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanProductVendorMapper {


    @Mapping(source = "vendorEntity", target = "vendor")
    @Mapping(source = "loanProductEntity", target = "loanProduct")
    LoanProductVendor map(LoanProductVendorEntity loanProductVendor);

}
