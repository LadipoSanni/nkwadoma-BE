package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanBook.RepaymentHistoryResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanBook.RepaymentScheduleEntry;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanBook.RepaymentScheduleResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanBook.YearRangeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RepaymentHistoryRestMapper {

    RepaymentHistoryResponse toRepaymentResponse(RepaymentHistory repaymentHistory);

    YearRangeResponse toYearRange(RepaymentHistory repaymentHistory);

    @Mapping(target = "repaymentDate", source = "paymentDate")
    @Mapping(target = "principalAmount", source = "principalPayment")
    @Mapping(target = "expectedMonthlyAmount", source = "amountPaid")
    @Mapping(target = "amountOutstanding", source = "amountOutstanding")
    @Mapping(target = "totalAmountRepaid", source = "totalAmountRepaid")
    @Mapping(target = "principalPayment", source = "principalPayment")
    @Mapping(target = "interest", source = "interestIncurred")
    RepaymentScheduleEntry toRepaymentScheduleEntry(RepaymentHistory repaymentHistory);



}
