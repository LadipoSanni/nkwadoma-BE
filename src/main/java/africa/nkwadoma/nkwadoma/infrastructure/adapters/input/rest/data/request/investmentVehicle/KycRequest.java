package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.identity.Country;
import africa.nkwadoma.nkwadoma.domain.enums.identity.UserRelationship;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FinancierType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
public class KycRequest {
    @NotBlank(message= "Bank name is required")
    private String bankName;
    @NotBlank(message = "Bank number is required")
    private String bankNumber;
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    @NotBlank(message = "Address is required")
    private String Address;
    @NotBlank(message = "National identification number is required")
    private String nin;
    @NotBlank(message = "Bvn is required")
    private String bvn;
    @NotBlank(message = "Tax id is required")
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
    private String taxInformationNumber;
    //beneficial Entity
    private String entityName;
    private String  beneficialRcNumber;
    private Country countryOfIncorporation;

    //beneficial individual
    private String beneficialOwnerFirstName;
    private String beneficialOwnerLastName;
    private UserRelationship beneficialOwnerRelationship;
    private LocalDate beneficialOwnerDateOfBirth;
    @PositiveOrZero(message = "Percentage ownership or share cannot be negative")
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
