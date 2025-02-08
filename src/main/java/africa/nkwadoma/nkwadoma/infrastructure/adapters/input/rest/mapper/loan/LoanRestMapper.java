package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanRestMapper {
    Loan toLoan(LoanQueryRequest loanQueryRequest);

    @Mapping(target = "userIdentity", source = "userIdentity")
    @Mapping(target = "cohortName", source = "cohortName")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "loaneeLoanBreakDowns", source = "loaneeLoanBreakdowns")
    LoanQueryResponse toLoanQueryResponse(Loan loan);
}
