package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ServiceOfferingMapper {
    @Mapping(target = "transactionLowerBound", defaultValue = "0")
    @Mapping(target = "transactionUpperBound", defaultValue = "0")
    ServiceOfferingEntity toServiceOfferingEntity(ServiceOffering serviceOffering);
}
