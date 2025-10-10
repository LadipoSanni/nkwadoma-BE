package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.disbursement;

import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.loan.disbursement.DisbursementRule;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.disbursement.DisbursementRuleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DisbursementRuleMapper {
    DisbursementRuleEntity map(DisbursementRule disbursementRule);

    DisbursementRule map(DisbursementRuleEntity disbursementRuleEntity);

    void edit(@MappingTarget DisbursementRule foundDIsbursementRule, DisbursementRule disbursementRule);

}
