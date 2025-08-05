package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier;

import africa.nkwadoma.nkwadoma.domain.enums.identity.Country;
import africa.nkwadoma.nkwadoma.domain.enums.identity.UserRelationship;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FinancierType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
public class BeneficialOwnerEntity {
    @Id
    @UuidGenerator
    private String id;

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
    private LocalDateTime beneficialOwnerDateOfBirth;
    @Column(nullable = false, columnDefinition = "double precision default 0.0")
    private double percentageOwnershipOrShare;
    //    Gov ID
    private String votersCard;
    private String nationalIdCard;
    private String driverLicense;

}
