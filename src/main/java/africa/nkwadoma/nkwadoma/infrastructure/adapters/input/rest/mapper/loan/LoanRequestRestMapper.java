package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanRequestRestMapper {
    @Mapping(target = "cohortStartDate", source = "cohort.startDate")
    @Mapping(target = "firstName", source = "loanee.userIdentity.firstName")
    @Mapping(target = "lastName", source = "loanee.userIdentity.lastName")
    LoanRequestResponse toLoanRequestResponse(LoanRequest loanRequest);
}
