package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.loanBook;

import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.DailyInterest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoaneeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",uses = {LoaneeMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DailyInterestMapper {


    DailyInterestEntity toDailyInterestEntity(DailyInterest dailyInterest);
}
