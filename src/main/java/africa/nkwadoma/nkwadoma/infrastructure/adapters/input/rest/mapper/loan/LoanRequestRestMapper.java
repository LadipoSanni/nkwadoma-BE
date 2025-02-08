package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanRequestRestMapper {

    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "userIdentity", source = "userIdentity")
    @Mapping(target = "cohortName", source = "cohortName")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "loaneeLoanBreakdowns", source = "loaneeLoanBreakdowns")
    @Mapping(target = "creditScore", source = "creditScore")
    LoanRequestResponse toLoanRequestResponse(LoanRequest loanRequest);

    @Mapping(target = "id", source = "loanRequestId")
    @Mapping(target = "loanProductId", source = "loanProductId")
    @Mapping(target = "loanAmountApproved", source = "amountApproved")
    LoanRequest toLoanRequest(LoanRequestDto loanRequestDto);
}
