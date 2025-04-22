package africa.nkwadoma.nkwadoma.domain.model.financier;

import africa.nkwadoma.nkwadoma.domain.enums.identity.Country;
import africa.nkwadoma.nkwadoma.domain.enums.identity.UserRelationship;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FinancierType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@Builder
@AllArgsConstructor

@RequiredArgsConstructor
public class BeneficialOwner {
    private String id;
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

    public void validate() {
        log.warn("Nothing being validated at the beneficial owner object");
    }
}
