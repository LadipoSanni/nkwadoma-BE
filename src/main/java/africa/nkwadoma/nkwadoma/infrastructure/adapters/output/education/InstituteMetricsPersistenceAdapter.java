package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.InstituteMetricsOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.InstituteMetrics;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.education.InstituteMetricsMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.InstituteMetricsEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.InstituteMetricsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class InstituteMetricsPersistenceAdapter implements InstituteMetricsOutputPort {


    private final InstituteMetricsMapper instituteMetricsMapper;
    private final InstituteMetricsRepository instituteMetricsRepository;

    @Override
    public InstituteMetrics save(InstituteMetrics instituteMetrics) throws MeedlException {
        MeedlValidator.validateObjectInstance(instituteMetrics,"Institute metrics cannot be empty");
        MeedlValidator.validateObjectInstance(instituteMetrics.getOrganization(),"Organization identity cannot be empty");

        InstituteMetricsEntity instituteMetricsEntity =
                instituteMetricsMapper.toInstituteMetricsEntity(instituteMetrics);
        instituteMetricsEntity = instituteMetricsRepository.save(instituteMetricsEntity);


        return instituteMetricsMapper.toInstituteMetrics(instituteMetricsEntity);
    }

    @Override
    public void delete(String id) throws MeedlException {
        MeedlValidator.validateUUID(id,"Institute metrics id cannot be empty");
        instituteMetricsRepository.deleteById(id);
    }

    @Override
    public InstituteMetrics findByOrganizationId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id,"Organization id cannot be empty");
        InstituteMetricsEntity instituteMetricsEntity =
                instituteMetricsRepository.findByOrganizationId(id);
        return instituteMetricsMapper.toInstituteMetrics(instituteMetricsEntity);
    }
}
