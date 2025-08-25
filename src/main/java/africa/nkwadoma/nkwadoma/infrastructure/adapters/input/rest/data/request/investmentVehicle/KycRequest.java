package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.model.financier.BeneficialOwner;
import africa.nkwadoma.nkwadoma.domain.model.financier.PoliticallyExposedPerson;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.Set;


@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
public class KycRequest {
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    @NotBlank(message = "Address is required")
    private String address;
    @NotBlank(message = "National identification number is required")
    private String nin;
    @NotBlank(message = "TIN is required")
    private String tin;
    @NotBlank(message = "Bvn is required")
    private String bvn;
    @NotBlank(message = "Tax id is required")
    private String taxId;
    private String rcNumber;
    //source of fund
    private Set<String> sourceOfFunds;

    private String taxInformationNumber;

    private List<BeneficialOwner> beneficialOwners;

    //Declaration
    private boolean declarationAndAgreement;
    private boolean politicallyExposed;
    private List<PoliticallyExposedPerson> politicallyExposedPeople;

}

