package africa.nkwadoma.nkwadoma.testUtilities.data;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyBvnResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyFaceData;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyNinResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.Verification;

public class VerificationMockData {

    public static PremblyBvnResponse createPremblyBvnTestResponse() {
        return PremblyBvnResponse.builder()
                .verificationCallSuccessful(true)
                .detail("Verification successful")
                .responseCode("00")
                .data(PremblyBvnResponse.BvnData.builder()
                        .bvn("12345678901")
                        .firstName("John")
                        .middleName("Doe")
                        .lastName("Smith")
                        .dateOfBirth("1990-01-01")
                        .registrationDate("2020-05-15")
                        .enrollmentBank("First Bank")
                        .enrollmentBranch("Lagos Main")
                        .email("john.doe@example.com")
                        .gender("Male")
                        .levelOfAccount("Tier 3")
                        .lgaOfOrigin("Ikeja")
                        .lgaOfResidence("Surulere")
                        .maritalStatus("Single")
                        .nin("12345678910")
                        .nameOnCard("John D. Smith")
                        .nationality("Nigerian")
                        .phoneNumber1("+2348012345678")
                        .phoneNumber2("+2348098765432")
                        .residentialAddress("123, Lagos Street, Ikeja")
                        .stateOfOrigin("Lagos")
                        .stateOfResidence("Lagos")
                        .title("Mr.")
                        .watchListed("No")
                        .image("base64-image-string")
                        .number("12345")
                        .faceData(createMockFaceData()) // Assuming an empty instance is okay
                        .build())
                .verification(createMockVerification()) // Assuming no data for verification
                .session(null) // Assuming no session data
                .build();
    }

    public static Verification createMockVerification() {
        return Verification.builder()
                .status("VERIFIED")
                .validIdentity(true) // This will be updated dynamically if updateValidIdentity() is called
                .reference("REF-123456345")
                .build();
    }
    public static PremblyFaceData createMockFaceData() {
        return PremblyFaceData.builder()
                .faceVerified(true)
                .message("Face Match")
                .confidence("99.9987564086914")
                .responseCode("00")
                .build();
    }

    public static PremblyNinResponse createPremblyNinTestResponse() {
        return PremblyNinResponse.builder()
                .verificationCallSuccessful(true)
                .detail("Verification successful")
                .responseCode("00")
                .ninData(PremblyNinResponse.NinData.builder()
                        .nin("12345678901")
                        .firstname("John")
                        .middleName("Doe")
                        .lastname("Smith")
                        .birthDate("1990-01-01")
                        .birthCountry("Nigeria")
                        .birthState("Lagos")
                        .birthLGA("Ikeja")
                        .gender("Male")
                        .email("john.doe@example.com")
                        .telephoneNo("+2348012345678")
                        .residenceAddress("123, Lagos Street, Ikeja")
                        .residenceState("Lagos")
                        .residenceLGA("Ikeja")
                        .residenceTown("Ikeja")
                        .maritalStatus("Single")
                        .employmentStatus("Employed")
                        .educationalLevel("Bachelor's Degree")
                        .profession("Software Engineer")
                        .selfOriginState("Lagos")
                        .selfOriginLGA("Ikeja")
                        .selfOriginPlace("Mainland")
                        .signature("base64-signature-string")
                        .photo("base64-photo-string")
                        .trackingId("TRACK-123456")
                        .userId("USR-987654")
                        .vnin("VNIN-54321")
                        .nokFirstName("Jane")
                        .nokSurname("Doe")
                        .nokMiddleName("Ann")
                        .nokAddress1("456, Abuja Street, FCT")
                        .nokAddress2("Suite 202")
                        .nokState("Abuja")
                        .nokLGA("Municipal")
                        .nokPostalCode("900001")
                        .nokTown("Central Area")
                        .spokenLanguage("English")
                        .build())
                .faceData(createMockFaceData())
                .verification(createMockVerification())
                .session(null)
                .endpointName("NIN Verification")
                .userId("USR-987654")
                .build();
    }
}
