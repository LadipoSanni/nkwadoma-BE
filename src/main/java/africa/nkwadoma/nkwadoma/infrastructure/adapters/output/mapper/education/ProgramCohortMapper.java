package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.education;


import africa.nkwadoma.nkwadoma.domain.model.education.ProgramCohort;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramCohortEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProgramCohortMapper {
    ProgramCohort toProgramCohort(ProgramCohortEntity programCohortEntity);

    List<ProgramCohort> toProgramCohortList(List<ProgramCohortEntity> programCohortEntities);

    ProgramCohortEntity toProgramCohortEntity(ProgramCohort programCohort1);
}
