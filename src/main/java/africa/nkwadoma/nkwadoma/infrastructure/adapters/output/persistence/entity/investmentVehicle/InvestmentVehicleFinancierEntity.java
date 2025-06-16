package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleDesignation;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class InvestmentVehicleFinancierEntity {
    @Id
    @UuidGenerator
    private String id;
    private BigDecimal amountInvested;
    @ManyToOne
    private FinancierEntity financier;
    @ManyToOne
    private InvestmentVehicleEntity investmentVehicle;
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<InvestmentVehicleDesignation> investmentVehicleDesignation;
    private LocalDate dateInvested;
}
