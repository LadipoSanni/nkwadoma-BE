package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.*;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanReferralRestMapper {
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    LoanReferralResponse toLoanReferralResponse(LoanReferral loanReferral);

    List<LoanReferralResponse> toLoanReferralResponses(Page<LoanReferral> loanReferrals);


    @Mapping(target = "amountReferred", source = "loanAmountRequested")
    @Mapping(target = "referralId", source = "id")
    AllLoanReferralResponse allLoanReferralResponse(LoanReferral loanReferral);
}
