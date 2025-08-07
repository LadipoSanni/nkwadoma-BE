package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle;

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
    @ElementCollection(targetClass = InvestmentVehicleDesignation.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "investment_vehicle_financier_entity_investment_vehicle_designation",
            joinColumns = @JoinColumn(name = "investment_vehicle_financier_entity_id")
    )
    @Column(name = "investment_vehicle_designation")
    private Set<InvestmentVehicleDesignation> investmentVehicleDesignation;

    private LocalDate dateInvested;
}
