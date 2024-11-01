package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;


import africa.nkwadoma.nkwadoma.domain.model.education.ProgramCohort;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramCohortEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProgramCohortMapper {
    ProgramCohort toProgramCohort(ProgramCohortEntity programCohortEntity);

    List<ProgramCohort> toProgramCohortList(List<ProgramCohortEntity> programCohortEntities);

    ProgramCohortEntity toProgramCohortEntity(ProgramCohort programCohort1);
}
