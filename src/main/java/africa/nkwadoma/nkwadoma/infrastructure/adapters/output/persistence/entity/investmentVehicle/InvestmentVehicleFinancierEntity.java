package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleDesignation;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

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
    @OneToOne
    private UserEntity financier;
    @ManyToOne
    private InvestmentVehicleEntity investmentVehicle;
    private InvestmentVehicleDesignation investmentVehicleRole;
}
