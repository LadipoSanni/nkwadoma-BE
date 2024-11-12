package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class NextOfKinRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String nextOfKinRelationship;
    private String contactAddress;
    private String alternateEmail;
    private String alternatePhoneNumber;
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

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress.trim();
    }

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
