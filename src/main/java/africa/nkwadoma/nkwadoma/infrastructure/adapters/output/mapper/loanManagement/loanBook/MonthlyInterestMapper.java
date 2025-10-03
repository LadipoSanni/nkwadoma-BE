package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.MonthlyInterest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.MonthlyInterestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MonthlyInterestMapper {


    MonthlyInterestEntity toMonthlyInterestEntity(MonthlyInterest monthlyInterest);

    MonthlyInterest toMonthlyInterest(MonthlyInterestEntity monthlyInterestEntity);
}
