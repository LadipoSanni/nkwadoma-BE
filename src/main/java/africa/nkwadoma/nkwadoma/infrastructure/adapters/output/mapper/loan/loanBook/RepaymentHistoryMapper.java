package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.loanBook;

import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.RepaymentHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RepaymentHistoryMapper {

    RepaymentHistoryEntity map(RepaymentHistory repaymentHistory);

    RepaymentHistory map(RepaymentHistoryEntity repaymentHistoryEntity);
}
