package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.loanProduct;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanProductVendorMapper {
}
