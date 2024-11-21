package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loan;


import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.LoanOfferResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanOfferRestMapper {
    LoanOfferResponse toLoanOfferResponse(LoanOffer loanOffer);
}
