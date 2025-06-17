package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.identity.Country;
import africa.nkwadoma.nkwadoma.domain.enums.identity.UserRelationship;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FinancierType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KycResponse {
    private String firstName;
    private String lastName;
    private String financierEmail;
    private String phoneNumber;
    private String Address;
    private String contactAddress;
    private String nin;
    private String bvn;
    private String taxId;

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
    private FinancierType beneficialOwnerType;
    //beneficial Entity
    private String entityName;
    private String beneficialRcNumber;
    private String taxInformationNumber;
    private Country countryOfIncorporation;

    //beneficial individual
    private String beneficialOwnerFirstName;
    private String beneficialOwnerLastName;
    private UserRelationship beneficialOwnerRelationship;
    private LocalDate beneficialOwnerDateOfBirth;
    private double percentageOwnershipOrShare;
    //    Gov ID
    private String votersCard;
    private String nationalIdCard;
    private String driverLicensetionalIdCard;
    private String driverLicense;

    //Declaration
    private boolean declarationAndAgreement;
    private boolean politicallyExposed;
}
