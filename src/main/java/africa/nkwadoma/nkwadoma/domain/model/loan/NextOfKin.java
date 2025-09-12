package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.identity.UserRelationship;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LevelOfEducation;
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
    private String stateOfResidence;
    private String levelOfEduction;
//    private UserRelationship nextOfKinRelationship;
    private String contactAddress;
    private String userId;
    private String alternateContactAddress;
    private String alternatePhoneNumber;
    private String alternateEmail;

    public void validate() throws MeedlException {
        String nextOfKin = "Next of kin ";
        MeedlValidator.validateDataElement(firstName, nextOfKin.concat("first name is required"));
        MeedlValidator.validateDataElement(lastName, nextOfKin.concat("last name is required"));
        MeedlValidator.validateEmail(email);
        MeedlValidator.validateDataElement(phoneNumber, nextOfKin.concat("phone number is required"));
        MeedlValidator.validateDataElement(nextOfKinRelationship, nextOfKin.concat("relationship should be defined"));
        MeedlValidator.validateDataElement(contactAddress, nextOfKin.concat("contact address is required"));

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

}