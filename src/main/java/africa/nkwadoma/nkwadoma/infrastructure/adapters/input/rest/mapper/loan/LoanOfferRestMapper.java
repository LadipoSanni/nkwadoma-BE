package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loan;


import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.LoanOfferResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoanOfferAcceptRequest;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanOfferRestMapper {

    LoanOfferResponse toLoanOfferResponse(LoanOffer loanOffer);
    @Mapping(target = "id", source = "loanOfferId")
    LoanOffer toLoanOffer(@Valid LoanOfferAcceptRequest loanOfferRequest);
    List<LoanOfferResponse> toLoanOfferResponses(Page<LoanOffer> loanOffers);
}
