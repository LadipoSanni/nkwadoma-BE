package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class PremblyNinResponse extends PremblyResponse{

    @JsonProperty("status")
    private boolean verificationCallSuccessful;

    @JsonProperty("detail")
    private String detail;

    @JsonProperty("response_code")
    private String responseCode;

    @JsonProperty("nin_data")
    private NinData ninData;

    @JsonProperty("face_data")
    private PremblyFaceData faceData;

    @JsonProperty("verification")
    private Verification verification;

    @JsonProperty("session")
    private Object session;

    @JsonProperty("endpoint_name")
    private String endpointName;

    private String userId;

    @Data
    @Builder
    @ToString
    public static class NinData {

        @JsonProperty("title")
        private String title;

        @JsonProperty("surname")
        private String lastname;

        @JsonProperty("firstname")
        private String firstname;

        @JsonProperty("gender")
        private String gender;

        @JsonProperty("birthcountry")
        private String birthCountry;

        @JsonProperty("birthdate")
        private String birthDate;

        @JsonProperty("birthlga")
        private String birthLGA;

        @JsonProperty("birthstate")
        private String birthState;

        @JsonProperty("centralID")
        private String centralID;

        @JsonProperty("educationallevel")
        private String educationalLevel;

        @JsonProperty("email")
        private String email;

        @JsonProperty("nin")
        private String nin;

        @JsonProperty("employmentstatus")
        private String employmentStatus;

        @JsonProperty("heigth")
        private String height;

        @JsonProperty("maritalstatus")
        private String maritalStatus;

        @JsonProperty("photo")
        private String photo;

        @JsonProperty("middlename")
        private String middleName;

        @JsonProperty("religion")
        private String religion;

        @JsonProperty("telephoneno")
        private String telephoneNo;

        @JsonProperty("residence_address")
        private String residenceAddress;

        @JsonProperty("residence_lga")
        private String residenceLGA;

        @JsonProperty("residence_state")
        private String residenceState;

        @JsonProperty("residence_town")
        private String residenceTown;

        @JsonProperty("residencestatus")
        private String residenceStatus;

        @JsonProperty("self_origin_lga")
        private String selfOriginLGA;

        @JsonProperty("self_origin_place")
        private String selfOriginPlace;

        @JsonProperty("self_origin_state")
        private String selfOriginState;

        @JsonProperty("signature")
        private String signature;

        @JsonProperty("spoken_language")
        private String spokenLanguage;

        @JsonProperty("nok_address1")
        private String nokAddress1;

        @JsonProperty("nok_address2")
        private String nokAddress2;

        @JsonProperty("nok_firstname")
        private String nokFirstName;

        @JsonProperty("nok_lga")
        private String nokLGA;

        @JsonProperty("nok_middlename")
        private String nokMiddleName;

        @JsonProperty("nok_postalcode")
        private String nokPostalCode;

        @JsonProperty("nok_state")
        private String nokState;

        @JsonProperty("nok_surname")
        private String nokSurname;

        @JsonProperty("nok_town")
        private String nokTown;

        @JsonProperty("ospokenlang")
        private String oSpokenLang;

        @JsonProperty("pfirstname")
        private String pFirstName;

        @JsonProperty("pmiddlename")
        private String pMiddleName;

        @JsonProperty("profession")
        private String profession;

        @JsonProperty("psurname")
        private String pSurname;

        @JsonProperty("trackingId")
        private String trackingId;

        @JsonProperty("userid")
        private String userId;

        @JsonProperty("vnin")
        private String vnin;
    }
    }

