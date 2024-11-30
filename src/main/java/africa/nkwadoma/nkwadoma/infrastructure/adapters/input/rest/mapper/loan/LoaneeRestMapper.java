package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoaneeRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.LoaneeReferralResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.LoaneeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoaneeRestMapper {

    Loanee toLoanee(LoaneeRequest loaneeRequest);

    LoaneeResponse toLoaneeResponse(Loanee loanee);

    LoaneeReferralResponse toLoaneeReferralResponse(LoanReferral loanReferral);

    List<LoaneeResponse> toLoaneeResponses(List<Loanee> loanee);
}
