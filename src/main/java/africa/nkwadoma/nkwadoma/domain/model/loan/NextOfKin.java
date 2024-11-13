package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import lombok.*;
import org.apache.commons.lang3.*;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NextOfKin {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String nextOfKinRelationship;
    private String contactAddress;
    private Loanee loanee;

    public void validate() throws MeedlException {
        MeedlValidator.validateDataElement(firstName);
        MeedlValidator.validateDataElement(lastName);
        MeedlValidator.validateDataElement(email);
        MeedlValidator.validateEmail(email);
        MeedlValidator.validateDataElement(phoneNumber);
        MeedlValidator.validateDataElement(nextOfKinRelationship);
        MeedlValidator.validateDataElement(contactAddress);
        MeedlValidator.validateDataElement(loanee.getUserIdentity().getAlternateContactAddress());
        MeedlValidator.validateDataElement(loanee.getUserIdentity().getAlternatePhoneNumber());
        MeedlValidator.validateDataElement(loanee.getUserIdentity().getAlternateEmail());
        MeedlValidator.validateDataElement(loanee.getUserIdentity().getId());
        MeedlValidator.validateUUID(loanee.getUserIdentity().getId());
    }

    public void setId(String id) {
        this.id = id.trim();
    }

    public void setFirstName(String firstName) {
        if (StringUtils.isNotEmpty(firstName)) {
            this.firstName = firstName.trim();
        }
    }

    public void setLastName(String lastName) {
        if (StringUtils.isNotEmpty(lastName)) {
            this.lastName = lastName.trim();
        }
    }

    public void setEmail(String email) {
        if (StringUtils.isNotEmpty(email)) {
            this.email = email.trim();
        }
    }

    public void setPhoneNumber(String phoneNumber) {
        if (StringUtils.isNotEmpty(phoneNumber)) {
            this.phoneNumber = phoneNumber.trim();
        }
    }

    public void setNextOfKinRelationship(String nextOfKinRelationship) {
        if (StringUtils.isNotEmpty(nextOfKinRelationship)) {
            this.nextOfKinRelationship = nextOfKinRelationship.trim();
        }
    }

    public void setContactAddress(String contactAddress) {
        if (StringUtils.isNotEmpty(contactAddress)) {
            this.contactAddress = contactAddress.trim();
        }
    }

    public void trimSpaceForUserIdentity(Loanee loanee) {
        loanee.getUserIdentity().setAlternateContactAddress(loanee.getUserIdentity().getAlternateContactAddress().trim());
        loanee.getUserIdentity().setAlternatePhoneNumber(loanee.getUserIdentity().getAlternatePhoneNumber().trim());
        loanee.getUserIdentity().setAlternateEmail(loanee.getUserIdentity().getAlternateEmail().trim());
    }
}