package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.education;

import africa.nkwadoma.nkwadoma.domain.model.education.InstituteMetrics;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.InstituteMetricsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InstituteMetricsMapper {


    InstituteMetricsEntity toInstituteMetricsEntity(InstituteMetrics instituteMetrics);

    InstituteMetrics toInstituteMetrics(InstituteMetricsEntity instituteMetricsEntity);
}
