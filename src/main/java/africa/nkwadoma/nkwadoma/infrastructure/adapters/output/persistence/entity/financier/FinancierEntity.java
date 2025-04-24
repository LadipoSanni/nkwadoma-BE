package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier;

import africa.nkwadoma.nkwadoma.domain.enums.AccreditationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.Country;
import africa.nkwadoma.nkwadoma.domain.enums.identity.UserRelationship;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.model.financier.BeneficialOwner;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.CooperationEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
public class FinancierEntity {

    @Id
    @UuidGenerator
    private String id;
    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus;
    @Enumerated(EnumType.STRING)
    private AccreditationStatus accreditationStatus;
    @Enumerated(EnumType.STRING)
    private FinancierType financierType;
    @OneToOne
    private UserEntity userIdentity;

    //source of fund
    @ElementCollection
    List<String> sourceOfFunds;
//    private String personalOrJointSavings;
//    private String employmentIncome;
//    private String salesOfAssets;
//    private String donation;
//    private String inheritanceOrGift;
//    private String compensationOfLegalSettlements;
//    private BigDecimal profitFromLegitimateActivities;
//    private String occupation;

//    @OneToMany
//    private List<BeneficialOwnerEntity> beneficialOwners;

    //Declaration
    private boolean declarationAndAgreement;
    private boolean politicallyExposed;

    @OneToOne
    private CooperationEntity cooperation;
    private BigDecimal totalAmountInvested;
    private LocalDateTime createdAt;
}
