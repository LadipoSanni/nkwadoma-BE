package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleVisibility;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.wallet.BankDetailEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier.FinancierEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FundRaisingStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private int tenure;
    private BigDecimal size;
    private BigDecimal totalAvailableAmount;
    private Float interestRateOffered;
    @Enumerated(EnumType.STRING)
    private FundRaisingStatus fundRaisingStatus;
    @OneToOne
    private FinancierEntity leads;
    @OneToOne
    private FinancierEntity contributors;
    private String trustee;
    private String custodian;
    private String bankPartner;
    private String fundManager;
    private BigDecimal minimumInvestmentAmount;
    private LocalDate startDate;
    private LocalDateTime createdDate;
    @Enumerated(EnumType.STRING)
    private InvestmentVehicleStatus investmentVehicleStatus;
    private String investmentVehicleLink;
    @OneToOne
    private VehicleOperationEntity operation;
    @OneToOne
    private VehicleClosureEntity closure;
    @OneToOne
    private BankDetailEntity mainAccount;
    @OneToOne
    private BankDetailEntity syncingAccount;
    private LocalDateTime lastUpdatedDate;
    @Enumerated(EnumType.STRING)
    private InvestmentVehicleVisibility investmentVehicleVisibility;
    private int talentFunded;
}
