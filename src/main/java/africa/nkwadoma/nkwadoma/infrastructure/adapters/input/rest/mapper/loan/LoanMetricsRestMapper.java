package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loan;


import africa.nkwadoma.nkwadoma.domain.model.loan.LoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.LoanDetailsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanMetricsRestMapper {

    List<LoanDetailsResponse> toLoanLifeCycleResponses(Page<LoanDetail> loanLifeCycles);
}
