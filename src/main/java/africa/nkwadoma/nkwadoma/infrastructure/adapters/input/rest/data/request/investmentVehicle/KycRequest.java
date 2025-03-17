package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.investmentVehicle;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

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
    private String financierName;
    private String financierEmail;
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    @NotBlank(message = "Address is required")
    private String Address;
    @NotBlank(message = "NextOfKin First name is required")
    private String nextOfKinFirstName;
    @NotBlank(message = "NextOfKin Last name is required")
    private String nextOfKinLastName;
    @NotBlank(message = "NextOfKin Phone number is required")
    private String NextOfKinPhoneNumber;
    @NotBlank(message = "NextOfKin Contact address is required")
    private String NextOfKinContactAddress;
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String NextOfKinEmail;
    @NotBlank(message = "Relationship with next of kin is required")
    private String relationshipWithNextOfKin;
    @NotBlank(message = "National identification number is required")
    private String nin;
    @NotBlank(message = "Tax id is required")
    private String taxId;

}
