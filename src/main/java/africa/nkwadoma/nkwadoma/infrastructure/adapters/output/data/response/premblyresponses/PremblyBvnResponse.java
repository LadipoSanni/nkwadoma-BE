package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PremblyBvnResponse extends PremblyResponse{

    @JsonProperty("status")
    private boolean verificationCallSuccessful;

    @JsonProperty("detail")
    private String detail;

    @JsonProperty("response_code")
    private String responseCode;

    @JsonProperty("data")
    private BvnData data;

    @JsonProperty("verification")
    private Verification verification;

    @JsonProperty("session")
    private Object session;

    @Data
    @Builder
    public static class BvnData {
        @JsonProperty("bvn")
        private String bvn;

        @JsonProperty("firstName")
        private String firstName;

        @JsonProperty("middleName")
        private String middleName;

        @JsonProperty("lastName")
        private String lastName;

        @JsonProperty("dateOfBirth")
        private String dateOfBirth;

        @JsonProperty("registrationDate")
        private String registrationDate;

        @JsonProperty("enrollmentBank")
        private String enrollmentBank;

        @JsonProperty("enrollmentBranch")
        private String enrollmentBranch;

        @JsonProperty("email")
        private String email;

        @JsonProperty("gender")
        private String gender;

        @JsonProperty("levelOfAccount")
        private String levelOfAccount;

        @JsonProperty("lgaOfOrigin")
        private String lgaOfOrigin;

        @JsonProperty("lgaOfResidence")
        private String lgaOfResidence;

        @JsonProperty("maritalStatus")
        private String maritalStatus;

        @JsonProperty("nin")
        private String nin;

        @JsonProperty("nameOnCard")
        private String nameOnCard;

        @JsonProperty("nationality")
        private String nationality;

        @JsonProperty("phoneNumber1")
        private String phoneNumber1;

        @JsonProperty("phoneNumber2")
        private String phoneNumber2;

        @JsonProperty("residentialAddress")
        private String residentialAddress;

        @JsonProperty("stateOfOrigin")
        private String stateOfOrigin;

        @JsonProperty("stateOfResidence")
        private String stateOfResidence;

        @JsonProperty("title")
        private String title;

        @JsonProperty("watchListed")
        private String watchListed;

        @JsonProperty("base64Image")
        private String image;

        @JsonProperty("number")
        private String number;

        @JsonProperty("face_data")
        private PremblyFaceData faceData;
    }


    }


