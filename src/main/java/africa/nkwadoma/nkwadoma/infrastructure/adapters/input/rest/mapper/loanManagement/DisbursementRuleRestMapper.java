package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement;

import africa.nkwadoma.nkwadoma.domain.model.loan.disbursement.DisbursementRule;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.DisbursementRuleRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.DisbursementRuleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DisbursementRuleRestMapper {
    @Mapping(target = "userIdentity.id", source = "userId")
    DisbursementRule map(String userId, DisbursementRuleRequest request);

    DisbursementRuleResponse map(DisbursementRule savedDisbursementRule);

    @Mapping(target = "userIdentity.id", source = "userId")
    DisbursementRule map(String userId, String id);
}
