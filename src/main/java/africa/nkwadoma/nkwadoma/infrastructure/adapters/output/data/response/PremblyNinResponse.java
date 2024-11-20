package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
public class PremblyNinResponse {

    private boolean status;

    private String detail;

    @JsonProperty("response_code")
    private String responseCode;

    @JsonProperty("nin_data")
    private NinData ninData;


    private Verification verification;

    @Data
    public static class NinData {
        @JsonProperty("birthcountry")
        private String birthCountry;

        @JsonProperty("birthdate")
        private String birthDate;

        @JsonProperty("educationallevel")
        private String educationalLevel;

        @JsonProperty("employmentstatus")
        private String employmentStatus;

        @JsonProperty("firstname")
        private String firstName;

        @JsonProperty("gender")
        private String gender;

        @JsonProperty("heigth")
        private String heigth;

        @JsonProperty("maritalstatus")
        private String maritalStatus;

        @JsonProperty("meedlName")
        private String meedlName;

        @JsonProperty("nin")
        private String nin;

        @JsonProperty("nok_address1")
        private String nokAddress1;

        @JsonProperty("nok_address2")
        private String nokAddress2;

        @JsonProperty("nok_firstname")
        private String nokFirstname;

        @JsonProperty("nok_lga")
        private String nokLga;

        @JsonProperty("nok_meedlname")
        private String nokMeedlname;

        @JsonProperty("nok_state")
        private String nokState;

        @JsonProperty("nok_surname")
        private String nokSurname;

        @JsonProperty("nok_town")
        private String nokTown;

        @JsonProperty("spoken_language")
        private String spokenLanguage;

        @JsonProperty("ospokenlang")
        private String oSpokenLang;

        @JsonProperty("profession")
        private String stateOfResidence;

        @JsonProperty("religion")
        private String religion;

        @JsonProperty("residence_address")
        private String residenceAddress;

        @JsonProperty("residence_town")
        private String residenceTown;

        @JsonProperty("residence_lga")
        private String residenceLga;

        @JsonProperty("residence_state")
        private String residenceState;

        @JsonProperty("residencestatus")
        private String residencesStatus;

        @JsonProperty("surname")
        private String surname;

        @JsonProperty("telephoneno")
        private String telephoneNo;

        @JsonProperty("title")
        private String title;

        @JsonProperty("trackingId")
        private String trackingId;

        @JsonProperty("vnin")
        private String vnin;

        @JsonProperty("pfirstname")
        private String pFirstname;

        @JsonProperty("psurname")
        private String pSurname;

        @JsonProperty("nok_postalcode")
        private String nokPostalCode;

        @JsonProperty("pmeedlname")
        private String pMeedlName;

        @JsonProperty("birthlga")
        private String birthLga;

        @JsonProperty("self_origin_state")
        private String selfOriginState;

        @JsonProperty("email")
        private String email;

        @JsonProperty("self_origin_place")
        private String selfOriginPlace;

        @JsonProperty("userid")
        private String userId;

        @JsonProperty("self_origin_lga")
        private String selfOriginLga;

        @JsonProperty("birthstate")
        private String birthState;
    }


    @Data
    public static class Verification {
        private String reference;
    }
}
