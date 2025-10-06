package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement;

import africa.nkwadoma.nkwadoma.domain.model.loan.DisbursementRule;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.DisbursementRuleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DisbursementRuleMapper {
    DisbursementRuleEntity map(DisbursementRule disbursementRule);

    DisbursementRule map(DisbursementRuleEntity disbursementRuleEntity);
}
