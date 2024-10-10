package africa.nkwadoma.nkwadoma.domain.model.education;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import lombok.*;

import java.util.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOffering {
    private String serviceOfferingId;
    private Industry industry;
    private ServiceOfferingType serviceOfferingType;
    private List<ServiceOfferingType> serviceOfferings = new ArrayList<>();
}
