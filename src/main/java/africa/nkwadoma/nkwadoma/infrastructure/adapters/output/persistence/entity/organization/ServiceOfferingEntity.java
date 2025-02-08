package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.*;

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
    private String id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Industry industry;
    private BigDecimal transactionLowerBound = BigDecimal.ZERO;
    private BigDecimal transactionUpperBound = BigDecimal.ZERO;
}
