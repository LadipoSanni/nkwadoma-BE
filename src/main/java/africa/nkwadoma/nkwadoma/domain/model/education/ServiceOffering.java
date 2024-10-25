package africa.nkwadoma.nkwadoma.domain.model.education;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import lombok.*;

import java.math.*;
import java.util.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOffering {
    private String id;
    private String name;
    private Industry industry;
//    private ServiceOfferingType serviceOfferingType;
    private BigDecimal transactionLowerBound;
    private BigDecimal transactionUpperBound;
}
