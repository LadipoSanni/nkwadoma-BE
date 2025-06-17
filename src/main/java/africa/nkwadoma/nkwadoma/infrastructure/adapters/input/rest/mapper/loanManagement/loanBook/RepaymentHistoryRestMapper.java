package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanBook.RepaymentHistoryResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanBook.YearRangeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RepaymentHistoryRestMapper {

    @Mapping(target = "firstName", source = "loanee.userIdentity.firstName")
    @Mapping(target = "lastName", source = "loanee.userIdentity.lastName")
    RepaymentHistoryResponse toRepaymentResponse(RepaymentHistory repaymentHistory);

    YearRangeResponse toYearRange(RepaymentHistory repaymentHistory);
}
