package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.bankdetail;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrganizationBankDetailMapper {
}
