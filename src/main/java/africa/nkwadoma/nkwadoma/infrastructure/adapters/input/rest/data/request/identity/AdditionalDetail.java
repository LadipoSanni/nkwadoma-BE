package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import africa.nkwadoma.nkwadoma.domain.enums.loanee.LevelOfEducation;
import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class AdditionalDetail {
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    @NotNull(message = "Relationship with next of kin is required")
    private String nextOfKinRelationship;
    private String contactAddress;
    private String stateOfResidence;
    private LevelOfEducation levelOfEduction;
    @Email(message = "Please provide a valid alternate email address")
    @NotBlank(message = "Alternate email address is required")
    private String alternateEmail;
    @NotBlank(message = "Alternate phone number is required")
    private String alternatePhoneNumber;
    @NotBlank(message = "Alternate contact address is required")
    private String alternateContactAddress;

    public void setFirstName(String firstName) {
        this.firstName = firstName.trim();
    }

    public void setLastName(String lastName) {
        this.lastName = lastName.trim();
    }

    public void setEmail(String email) {
        this.email = email.trim();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber.trim();
    }

    public void setNextOfKinRelationship(String nextOfKinRelationship) {
        this.nextOfKinRelationship = nextOfKinRelationship.trim();
    }

//    public void setContactAddress(String contactAddress) {
//        this.contactAddress = contactAddress.trim();
//    }

    public void setAlternateEmail(String alternateEmail) {
        this.alternateEmail = alternateEmail.trim();
    }

    public void setAlternatePhoneNumber(String alternatePhoneNumber) {
        this.alternatePhoneNumber = alternatePhoneNumber.trim();
    }

    public void setAlternateContactAddress(String alternateContactAddress) {
        this.alternateContactAddress = alternateContactAddress.trim();
    }
}
