package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.InstituteMetrics;

public interface InstituteMetricsOutputPort {
    InstituteMetrics save(InstituteMetrics saveInstituteMetrics) throws MeedlException;

    void delete(String id) throws MeedlException;

    void deleteByOrganizationId(String organizationId) throws MeedlException;

    InstituteMetrics findByOrganizationId(String id) throws MeedlException;
}
