package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.*;
import org.mapstruct.*;

import java.util.*;

@Mapper(componentModel = "spring")
public interface ServiceOfferingMapper {
    @Mapping(target = "transactionLowerBound", defaultValue = "0")
    @Mapping(target = "transactionUpperBound", defaultValue = "0")
    @Mapping(target = "industry", source = "industry")
    ServiceOfferingEntity toServiceOfferingEntity(ServiceOffering serviceOffering);

//    List<String> toServiceOfferingIds(List<ServiceOffering> serviceOfferings);

    default List<String> mapServiceOffering(List<ServiceOffering> serviceOfferings) {
        return serviceOfferings.stream().map(ServiceOffering::getId).toList();
    }
}
