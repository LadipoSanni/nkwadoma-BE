package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.ServiceOfferingOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.ServiceOfferingEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.ServiceOfferingMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.ServiceOfferEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class ServiceOfferingAdapter implements ServiceOfferingOutputPort {


    private final ServiceOfferEntityRepository serviceOfferEntityRepository;
    private final ServiceOfferingMapper serviceOfferingMapper;

    @Override
    public ServiceOffering save(ServiceOffering serviceOffering) {
        ServiceOfferingEntity serviceOfferingEntity =
                serviceOfferingMapper.toServiceOfferingEntity(serviceOffering);
        serviceOfferingEntity = serviceOfferEntityRepository.save(serviceOfferingEntity);
        return serviceOfferingMapper.toServiceOfferingEntity(serviceOfferingEntity);
    }
}
