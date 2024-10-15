package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CohortMapper {


    CohortEntity toCohortEntity(Cohort cohort);

    Cohort toCohort(CohortEntity cohortEntity);
}
