package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement;

import africa.nkwadoma.nkwadoma.domain.model.loan.disbursement.DisbursementRule;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.disbursement.ApplyDisbursementRuleRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.disbursement.RemoveDisbursementRule;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.disbursement.SetUpDisbursementRuleRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.disbursement.EditDisbursementRuleRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.DisbursementRuleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DisbursementRuleRestMapper {
    @Mapping(target = "userIdentity.id", source = "userId")
    DisbursementRule map(String userId, SetUpDisbursementRuleRequest request);

    @Mapping(target = "userIdentity.id", source = "userId")
    DisbursementRule map(String userId, EditDisbursementRuleRequest request);

    DisbursementRuleResponse map(DisbursementRule savedDisbursementRule);

    @Mapping(target = "userIdentity.id", source = "userId")
    DisbursementRule map(String userId, String id);

    @Mapping(target = "userIdentity.id", source = "userId")
    DisbursementRule map(String userId, ApplyDisbursementRuleRequest applyDisbursementRuleRequest);

    @Mapping(target = "userIdentity.id", source = "userId")
    DisbursementRule map(String  userId, RemoveDisbursementRule removeDisbursementRule);

}
