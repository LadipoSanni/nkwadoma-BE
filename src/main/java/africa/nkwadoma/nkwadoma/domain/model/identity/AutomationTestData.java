package africa.nkwadoma.nkwadoma.domain.model.identity;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyBvnResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyFaceData;
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


}
