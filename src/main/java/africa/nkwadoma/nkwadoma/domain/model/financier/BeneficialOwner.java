package africa.nkwadoma.nkwadoma.domain.model.financier;

import africa.nkwadoma.nkwadoma.domain.enums.identity.Country;
import africa.nkwadoma.nkwadoma.domain.enums.identity.UserRelationship;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FinancierType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor

@RequiredArgsConstructor
public class BeneficialOwner {
    //Beneficial owner information
    private FinancierType beneficialOwnerType;
    //beneficial Entity
    private String entityName;
    private String beneficialRcNumber;
    private Country countryOfIncorporation;

    //beneficial individual
    private String beneficialOwnerFirstName;
    private String beneficialOwnerLastName;
    private UserRelationship beneficialOwnerRelationship;
    private LocalDateTime beneficialOwnerDateOfBirth;
    private double percentageOwnershipOrShare;
    //    Gov ID
    private String votersCard;
    private String nationalIdCard;
    private String driverLicensetionalIdCard;
    private String driverLicense;
}
