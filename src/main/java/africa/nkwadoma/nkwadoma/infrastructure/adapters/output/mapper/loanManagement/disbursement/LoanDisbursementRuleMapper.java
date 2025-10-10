package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.disbursement;

import africa.nkwadoma.nkwadoma.domain.model.loan.disbursement.LoanDisbursementRule;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.LoanMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.disbursement.LoanDisbursementRuleEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {LoanMapper.class, DisbursementRuleMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanDisbursementRuleMapper {
    @Mapping(source = "loan", target = "loanEntity")
    @Mapping(source = "disbursementRule", target = "disbursementRuleEntity")
    LoanDisbursementRuleEntity map(LoanDisbursementRule loanDisbursementRule);

    @InheritInverseConfiguration
    LoanDisbursementRule map(LoanDisbursementRuleEntity loanDisbursementRuleEntity);
}
