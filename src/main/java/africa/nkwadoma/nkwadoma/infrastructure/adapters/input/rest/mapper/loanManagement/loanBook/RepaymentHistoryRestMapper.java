package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanBook.*;
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
    @Mapping(target = "interest", source = "interestIncurred")
    RepaymentScheduleEntry toRepaymentScheduleEntry(RepaymentHistory repaymentHistory);


    @Mapping(target = "totalInterestRepayment", source = "interestIncurred")
    @Mapping(target = "totalRepayment", source = "totalAmountRepaid")
    @Mapping(target = "monthlyRepayment", source = "principalPayment")
    SimulateRepaymentResponse toSimulateRepaymentResponse(RepaymentHistory repaymentHistory);
}
