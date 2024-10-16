package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle;



import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleFinancier;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.List;

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
    private InvestmentVehicleType investmentVehicleType;
    private String mandate;
    private String sponsors;
    private String tenure;
    private BigDecimal size;
    private Float rate;
    private FundRaisingStatus fundRaisingStatus;
    @OneToOne
    private InvestmentVehicleFinancierEntity leads;
    @OneToOne
    private InvestmentVehicleFinancierEntity contributors;

}
