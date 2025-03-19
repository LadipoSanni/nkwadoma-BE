package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KycResponse {
    private String bankName;
    private String bankNumber;
    private String financierName;
    private String financierEmail;
    private String phoneNumber;
    private String Address;
    private String nextOfKinFirstName;
    private String nextOfKinLastName;
    private String phone;
    private String contactAddress;
    private String email;
    private String relationshipWithNextOfKin;
    private String nin;
    private String taxId;
}
