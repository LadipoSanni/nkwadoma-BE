package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.RepaymentHistoryEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.loanBook.RepaymentHistoryProjection;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanee.LoaneeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",uses = {LoaneeMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RepaymentHistoryMapper {
    @Mapping(target = "cohortId", source = "cohort.id")
    RepaymentHistoryEntity map(RepaymentHistory repaymentHistory);

    RepaymentHistory map(RepaymentHistoryEntity repaymentHistoryEntity);

    @Mapping(target = "paymentDateTime" , source = "paymentDateTime")
    @Mapping(target = "amountPaid", source = "amountPaid")
    @Mapping(target = "modeOfPayment", source = "modeOfPayment")
    @Mapping(target = "amountOutstanding", source = "amountOutstanding")
    @Mapping(target = "totalAmountRepaid", source = "totalAmountRepaid")
    @Mapping(target = "firstYear", source = "firstYear")
    @Mapping(target = "lastYear", source = "lastYear")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "interestIncurred", source = "interestIncurred")
    RepaymentHistory mapProjecttionToRepaymentHistory(RepaymentHistoryProjection repaymentHistoryProjection);
}
