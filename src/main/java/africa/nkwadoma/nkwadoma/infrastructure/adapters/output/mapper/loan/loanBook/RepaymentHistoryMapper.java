package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.loanBook;

import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.RepaymentHistoryEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoaneeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",uses = {LoaneeMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RepaymentHistoryMapper {
    @Mapping(target = "cohortId", source = "cohort.id")
    RepaymentHistoryEntity map(RepaymentHistory repaymentHistory);

    RepaymentHistory map(RepaymentHistoryEntity repaymentHistoryEntity);
}
