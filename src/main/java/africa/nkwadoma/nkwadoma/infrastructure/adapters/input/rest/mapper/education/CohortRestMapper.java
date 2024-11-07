package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.education;

import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.CreateCohortRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.EditCohortLoanDetailRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education.CohortResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CohortRestMapper {


    Cohort toCohort(CreateCohortRequest createCohortRequest);

    CohortResponse toCohortResponse(Cohort cohort);

    Cohort mapEditCohortRequestToCohort(EditCohortLoanDetailRequest editCohortLoanDetailRequest);
}
