package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;


import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanOfferEntitiy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanOfferMapper {


    @Mapping(source = "status", target = "loanRequestStatus")
    @Mapping(source = "id", target = "loanRequestId")
    LoanOffer mapLoanRequestToLoanOffer(LoanRequest loanRequest);

    LoanOfferEntitiy toLoanOfferEntity(LoanOffer loanOffer);

    LoanOffer toLoanOffer(LoanOfferEntitiy loanOfferEntitiy);
}
