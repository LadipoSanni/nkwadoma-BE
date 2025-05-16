package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanReferralRestMapper {
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    LoanReferralResponse toLoanReferralResponse(LoanReferral loanReferral);

//    LoanReferral toLoanReferral(LoanReferralRequest request);
//
//    default LoanReferralStatus toLoanReferralStatus(String loanReferralStatus) throws MeedlException {
//        if (loanReferralStatus == null) {
//            return null;
//        }
//        try {
//            return LoanReferralStatus.valueOf(loanReferralStatus.toUpperCase());
//        } catch (IllegalArgumentException e) {
//            throw new MeedlException(ErrorMessages.INVALID_LOAN_REFERRAL_STATUS);
//        }
//    }
    @Mapping(target = "loanee.userIdentity.id", source = "userId")
    LoanReferral toLoanReferral(String userId);
}
