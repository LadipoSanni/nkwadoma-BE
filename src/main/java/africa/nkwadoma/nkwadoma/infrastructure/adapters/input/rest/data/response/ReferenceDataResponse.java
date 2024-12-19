package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response;

import africa.nkwadoma.nkwadoma.domain.enums.Industry;
import africa.nkwadoma.nkwadoma.domain.enums.ServiceOfferingType;
import lombok.Getter;

@Getter
public class ReferenceDataResponse {
    private final Industry[] industries = Industry.values();
    private final ServiceOfferingType[] serviceOfferingTypes = ServiceOfferingType.values();
}
