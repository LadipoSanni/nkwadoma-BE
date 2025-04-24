package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.identity.Country;
import africa.nkwadoma.nkwadoma.domain.enums.identity.UserRelationship;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.model.financier.BeneficialOwner;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    private String taxInformationNumber;

    private List<BeneficialOwner> beneficialOwners;

    //Declaration
    private boolean declarationAndAgreement;
    private boolean politicallyExposed;

}

