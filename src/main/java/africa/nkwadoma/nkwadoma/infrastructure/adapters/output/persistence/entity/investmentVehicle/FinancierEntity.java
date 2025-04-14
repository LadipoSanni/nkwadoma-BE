package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.AccreditationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.Country;
import africa.nkwadoma.nkwadoma.domain.enums.identity.UserRelationship;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FinancierType;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private String personalOrJointSavings;
    private String employmentIncome;
    private String salesOfAssets;
    private String donation;
    private String inheritanceOrGift;
    private String compensationOfLegalSettlements;
    private BigDecimal profitFromLegitimateActivities;

    private String occupation;


    //Beneficial owner information
    @Enumerated(EnumType.STRING)
    private FinancierType beneficialOwnerType;
    //Entity
    private String entityName;
    private String  beneficialRcNumber;
    @Enumerated(EnumType.STRING)
    private Country countryOfIncorporation;

    //beneficial individual
    private String beneficialOwnerFirstName;
    private String beneficialOwnerLastName;
    @Enumerated(EnumType.STRING)
    private UserRelationship beneficialOwnerRelationship;
    private LocalDate beneficialOwnerDateOfBirth;
    @Column(nullable = false, columnDefinition = "double precision default 0.0")
    private double percentageOwnershipOrShare;
    //    Gov ID
    private String votersCard;
    private String nationalIdCard;
    private String driverLicensetionalIdCard;
    private String driverLicense;

    //Declaration
    private boolean declarationAndAgreement;
    private boolean politicallyExposed;

    @OneToOne
    private CooperationEntity cooperation;
    private BigDecimal totalAmountInvested;
    private LocalDateTime createdAt;
}
