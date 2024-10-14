package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.*;
import java.util.*;
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ServiceOfferingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String serviceOfferingId;
    @Enumerated(EnumType.STRING)
    private Industry industry;
    @Enumerated(EnumType.STRING)
    private ServiceOfferingType serviceOfferingType;
    private BigDecimal transactionLowerBound = BigDecimal.ZERO;
    private BigDecimal transactionUpperBound = BigDecimal.ZERO;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> serviceOfferings = new ArrayList<>();
}
