package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.disbursement;

import africa.nkwadoma.nkwadoma.domain.model.loan.disbursement.LoanDisbursementRule;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.LoanMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.disbursement.LoanDisbursementRuleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {LoanMapper.class, DisbursementRuleMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanDisbursementRuleMapper {
    LoanDisbursementRuleEntity map(LoanDisbursementRule loanDisbursementRule);

    LoanDisbursementRule map(LoanDisbursementRuleEntity loanDisbursementRuleEntity);
}
