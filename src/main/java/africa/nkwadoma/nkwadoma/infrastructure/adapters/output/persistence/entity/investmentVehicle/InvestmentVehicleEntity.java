package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle;



import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    @Size( max = 2500, message = "Investment vehicle mandate must not exceed 2500 characters")
    private String mandate;
    private String sponsors;
    private int tenure;
    private BigDecimal size;
    private BigDecimal totalAvailableAmount;
    private Float rate;
    @Enumerated(EnumType.STRING)
    private FundRaisingStatus fundRaisingStatus;
    @OneToOne
    private InvestmentVehicleFinancierEntity leads;
    @OneToOne
    private InvestmentVehicleFinancierEntity contributors;
    private String trustee;
    private String custodian;
    private String bankPartner;
    private String fundManager;
    private BigDecimal minimumInvestmentAmount;
    private LocalDate startDate;
    @Enumerated(EnumType.STRING)
    private InvestmentVehicleStatus investmentVehicleStatus;
    private String investmentVehicleLink;


}
