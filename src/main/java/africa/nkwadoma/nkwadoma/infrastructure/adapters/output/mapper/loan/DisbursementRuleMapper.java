package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DisbursementRuleMapper {
}
