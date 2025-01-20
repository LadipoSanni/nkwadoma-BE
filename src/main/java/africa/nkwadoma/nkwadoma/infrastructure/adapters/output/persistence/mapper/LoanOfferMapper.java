package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;


import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanOfferEntitiy;
import org.mapstruct.*;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanOfferMapper {
    @Mapping(target = "dateTimeOffered", expression = "java(java.time.LocalDateTime.now())")
    LoanOfferEntitiy toLoanOfferEntity(LoanOffer loanOffer);

    LoanOffer toLoanOffer(LoanOfferEntitiy loanOfferEntitiy);

    void updateLoanOffer(@MappingTarget LoanOffer offer, LoanOffer loanOffer);
}
