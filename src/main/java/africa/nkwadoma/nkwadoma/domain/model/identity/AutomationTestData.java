package africa.nkwadoma.nkwadoma.domain.model.identity;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyBvnResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyFaceData;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyNinResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.Verification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AutomationTestData {

    public static PremblyBvnResponse createPremblyBvnTestResponse(String bvn) {
        return PremblyBvnResponse.builder()
                .verificationCallSuccessful(true)
                .detail("Verification successful")
                .responseCode("00")
                .data(PremblyBvnResponse.BvnData.builder()
                        .bvn(bvn)
                        .firstName("automatedTest")
                        .middleName("automatedTest")
                        .lastName("automatedTest")
                        .dateOfBirth("1990-01-01")
                        .registrationDate("2020-05-15")
                        .enrollmentBank("First Bank Automated Test")
                        .enrollmentBranch("Lagos Main Automated Test")
                        .email("john.doe@example.com")
                        .gender("Male")
                        .levelOfAccount("Tier 3")
                        .lgaOfOrigin("IkejaAutomatedTest")
                        .lgaOfResidence("SurulereAutomatedTest")
                        .maritalStatus("Single")
                        .nin("12345678910")
                        .nameOnCard("John D. Smith AutomatedTest")
                        .nationality("Nigerian")
                        .phoneNumber1("+2348012345678")
                        .phoneNumber2("+2348098765432")
                        .residentialAddress("123, Lagos Street, Ikeja AutomatedTest")
                        .stateOfOrigin("Lagos")
                        .stateOfResidence("Lagos")
                        .title("Mr.")
                        .watchListed("No")
                        .image("base64-image-string")
                        .number("12345")
                        .faceData(createMockFaceData())
                        .build())
                .verification(createMockVerification())
                .session(null)
                .build();
    }

    public static Verification createMockVerification() {
        return Verification.builder()
                .status("VERIFIED")
                .validIdentity(true)
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
