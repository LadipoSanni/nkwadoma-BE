package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle;



import africa.nkwadoma.nkwadoma.domain.enums.*;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleFinancier;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;

@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class InvestmentVehicleEntity {

    @Id
    @UuidGenerator
    private String id;
    private String name;
    @Enumerated(EnumType.STRING)
    private InvestmentVehicleType investmentVehicleType;
    private String mandate;
    private String sponsors;
    private String tenure;
    private BigDecimal size;
    private Float rate;
    @Enumerated(EnumType.STRING)
    private FundRaisingStatus fundRaisingStatus;
    @OneToOne
    private InvestmentVehicleFinancierEntity leads;
    @OneToOne
    private InvestmentVehicleFinancierEntity contributors;

}
