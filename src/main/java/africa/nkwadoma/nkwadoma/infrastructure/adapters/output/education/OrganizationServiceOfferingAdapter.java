package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.OrganizationServiceOfferingOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationServiceOffering;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.education.OrganizationServiceOfferingMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationServiceOfferingEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.ServiceOfferingMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.OrganizationServiceOfferingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class OrganizationServiceOfferingAdapter implements OrganizationServiceOfferingOutputPort {


    private final OrganizationServiceOfferingRepository organizationServiceOfferingRepository;
    private final OrganizationServiceOfferingMapper organizationServiceOfferingMapper;

    @Override
    public OrganizationServiceOffering save(OrganizationServiceOffering organizationServiceOffering) {

        OrganizationServiceOfferingEntity organizationServiceOfferingEntity =
                organizationServiceOfferingMapper.toOrganizationServiceOfferingEntity(organizationServiceOffering);
        organizationServiceOfferingEntity = organizationServiceOfferingRepository.save(organizationServiceOfferingEntity);

        return organizationServiceOfferingMapper.toOrganizationServiceOffering(organizationServiceOfferingEntity);
    }
}
