package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.loanBook;

import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.RepaymentHistoryEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.loanBook.RepaymentHistoryProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RepaymentHistoryMapper {
    @Mapping(target = "cohortId", source = "cohort.id")
    RepaymentHistoryEntity map(RepaymentHistory repaymentHistory);

    RepaymentHistory map(RepaymentHistoryEntity repaymentHistoryEntity);

    @Mapping(target = "firstName" , source = "firstName")
    @Mapping(target = "lastName" , source = "lastName")
    @Mapping(target = "paymentDateTime" , source = "paymentDateTime")
    @Mapping(target = "amountPaid", source = "amountPaid")
    @Mapping(target = "modeOfPayment", source = "modeOfPayment")
    @Mapping(target = "amountOutstanding", source = "amountOutstanding")
    @Mapping(target = "totalAmountRepaid", source = "totalAmountRepaid")
    @Mapping(target = "firstYear", source = "firstYear")
    @Mapping(target = "lastYear", source = "lastYear")
    @Mapping(target = "id", source = "id")
    RepaymentHistory mapProjecttionToRepaymentHistory(RepaymentHistoryProjection repaymentHistoryProjection);
}
