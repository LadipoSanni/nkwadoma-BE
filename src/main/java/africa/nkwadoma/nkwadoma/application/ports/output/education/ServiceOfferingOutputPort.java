package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;

public interface ServiceOfferingOutputPort {
    ServiceOffering save(ServiceOffering serviceOffering);
}
