package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationServiceOffering;

public interface OrganizationServiceOfferingOutputPort {
    OrganizationServiceOffering save(OrganizationServiceOffering organizationServiceOfferingEntity);
}
