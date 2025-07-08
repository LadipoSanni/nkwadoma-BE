package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityverificationmanager.verificaitonmockdata;

import africa.nkwadoma.nkwadoma.domain.enums.identity.Country;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyBvnResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyFaceData;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyNinResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.Verification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

public class VerificationMock {

    public static PremblyNinResponse createPremblyNinTestResponse(String nin) {
        return PremblyNinResponse.builder()
                .verificationCallSuccessful(true)
                .detail("Verification successful")
                .responseCode("00")
                .ninData(PremblyNinResponse.NinData.builder()
                        .nin(nin)
                        .firstname("John")
                        .middleName("Doe")
                        .lastname("Smith")
                        .birthDate(LocalDate.now().toString())
                        .birthCountry(getRandomCountryName())
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
    public static PremblyBvnResponse createPremblyBvnTestResponse(String bvn, String nin) {
        return PremblyBvnResponse.builder()
                .verificationCallSuccessful(true)
                .detail("Verification successful")
                .responseCode("00")
                .data(PremblyBvnResponse.BvnData.builder()
                        .bvn(bvn)
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
                        .nin(nin)
                        .nameOnCard("John D. Smith")
                        .nationality(getRandomCountryName())
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
    public static PremblyFaceData createMockFaceData() {
        return PremblyFaceData.builder()
                .faceVerified(true)
                .message("Face Match")
                .confidence("99.9987564086914")
                .responseCode("00")
                .build();
    }
    public static Verification createMockVerification() {
        return Verification.builder()
                .status("VERIFIED")
                .validIdentity(true) // This will be updated dynamically if updateValidIdentity() is called
                .reference("REF-123456345")
                .build();
    }
    public static String getRandomCountryName() {
        Country[] countries = Country.values();
        int randomIndex = ThreadLocalRandom.current().nextInt(countries.length);
        return countries[randomIndex].name();
    }
}
