package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanReferralRestMapper {
    LoanReferralResponse toLoanReferralResponse(LoanReferral loanReferral);
    @Mapping(target = "loanee.userIdentity.id", source = "userId")
    LoanReferral toLoanReferral(String userId);
}
