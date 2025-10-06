package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.loanProduct;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProductDisbursementRule;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanProductDisbursementRuleEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanProductDisbursementRuleMapper {
    @Mapping(target = "loanProductEntity", source = "loanProduct")
    @Mapping(target = "disbursementRuleEntity", source = "disbursementRule")
    LoanProductDisbursementRuleEntity map(LoanProductDisbursementRule loanProductDisbursementRule);

    @InheritInverseConfiguration
    LoanProductDisbursementRule map(LoanProductDisbursementRuleEntity loanProductDisbursementRuleEntity);
}
