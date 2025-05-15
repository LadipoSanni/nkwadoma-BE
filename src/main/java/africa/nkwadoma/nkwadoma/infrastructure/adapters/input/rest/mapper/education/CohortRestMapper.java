package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.education;

import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.CreateCohortRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.EditCohortRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education.CohortResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.LoanBreakdownResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CohortRestMapper {


    Cohort toCohort(CreateCohortRequest createCohortRequest);

    CohortResponse toCohortResponse(Cohort cohort);

    LoanBreakdownResponse toLoanBreakdownResponse(LoanBreakdown loanBreakdown);

    Cohort mapEditCohortRequestToCohort(EditCohortRequest editCohortRequest);

    List<CohortResponse> toCohortResponses(List<Cohort> cohorts);

    List<LoanBreakdownResponse> toLoanBreakdownResponses(List<LoanBreakdown> loanBreakdowns);
}
