package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.output.aes.AesOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoanDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramLoanDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanMetricsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanReferralOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ServiceProvider;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanMetrics;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.IdentityVerificationMapper;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages.IDENTITY_VERIFICATION_FAILURE_SAVED;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages.IDENTITY_NOT_VERIFIED;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages.IDENTITY_VERIFIED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class IdentityVerificationServiceTest {
    @InjectMocks
    private IdentityVerificationService identityVerificationService;
    @Mock
    private LoanReferralOutputPort loanReferralOutputPort;
    @Mock
    private IdentityVerificationOutputPort identityVerificationOutputPort;
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private IdentityVerificationFailureRecordOutputPort identityVerificationFailureRecordOutputPort;
    @Mock
    private AesOutputPort tokenUtils;
    @Mock
    private IdentityVerificationMapper identityVerificationMapper;
    @Mock
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Mock
    private LoanMetricsOutputPort loanMetricsOutputPort;
    @Mock
    private ProgramLoanDetailOutputPort programLoanDetailOutputPort;
    @Mock
    private OrganizationLoanDetailOutputPort organizationLoanDetailOutputPort;
    @Mock
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    @Mock
    private CohortLoanDetailOutputPort cohortLoanDetailOutputPort;
    @Mock
    private CohortOutputPort cohortOutputPort;
    @Mock
    private IdentityManagerOutputPort identityManagerOutputPort;
    private UserIdentity favour;
    private UserIdentity favour2;
    private LoanReferral loanReferral;
    private final String testId ="9c558b64-c207-4c34-99c7-8d2f04398496";
    private final String testBvn = "etlGGJ4BSGNxBkqfv3rPqw==";
    private final String testNin = "etlGGJ4BSGNxBkqfv3rPqw==";
    private IdentityVerification identityVerification;
    private IdentityVerificationFailureRecord identityVerificationFailureRecord;
    private CohortLoanee cohortLoanee;
    private LoaneeLoanDetail loaneeLoanDetail;
    private Cohort cohort;
    private CohortLoanDetail cohortLoanDetail;
    private ProgramLoanDetail programLoanDetail;
    private OrganizationLoanDetail organizationLoanDetail;
    private LoanMetrics loanMetrics;
    private OrganizationIdentity organizationIdentity;

    @BeforeEach
    void setUp() {
        favour = TestData.createTestUserIdentity("favour@gmail.com");
        favour2 = TestData.createTestUserIdentity("favour@gmail.com");
        Loanee loanee = TestData.createTestLoanee(favour, TestData.createTestLoaneeLoanDetail());
        loanee.setUserIdentity(favour);
        cohort = TestData.createCohortData("cohort",testId,testId,null,testId);
        loaneeLoanDetail = TestData.createTestLoaneeLoanDetail();
        cohortLoanee = TestData.buildCohortLoanee(loanee,cohort,loaneeLoanDetail,testId);
        cohortLoanee.setReferredBy("favour Org");
        loanReferral = LoanReferral.builder().cohortLoanee(cohortLoanee).id(testId).loanReferralStatus(LoanReferralStatus.AUTHORIZED).loanee(loanee).build();
        loanMetrics =TestData.createTestLoanMetrics(testId);
        cohortLoanDetail = TestData.buildCohortLoanDetail(cohort);
        Program program = TestData.createProgramTestData("prgram");
        programLoanDetail = TestData.buildProgramLoanDetail(program);
        programLoanDetail.setId(testId);
        organizationLoanDetail = TestData.buildOrganizationLoanDetail(new OrganizationIdentity());
        organizationIdentity = TestData.createOrganizationTestData("favour Org","RC1234567",
                List.of(new OrganizationEmployeeIdentity()));

        identityVerification = new IdentityVerification();
        identityVerification.setEncryptedBvn(testBvn);
        identityVerification.setEncryptedNin(testNin);
        identityVerification.setLoanReferralId(testId);

        identityVerificationFailureRecord = IdentityVerificationFailureRecord.builder()
                .email("test@example.com")
                .reason("wrong bvn")
                .userId(testId)
                .serviceProvider(ServiceProvider.SMILEID)
                .build();
    }

    @Test
    void verifyIdentitySuccessfulVerification() throws MeedlException {
        when(identityVerificationFailureRecordOutputPort.countByUserId(testId)).thenReturn(0L);

        when(tokenUtils.decryptAES(identityVerification.getEncryptedBvn(),"Error processing identity verification")).thenReturn("12345678901");
        when(tokenUtils.decryptAES(identityVerification.getEncryptedNin(),"Error processing identity verification")).thenReturn("12345678901");
        when(userIdentityOutputPort.findByBvn(identityVerification.getEncryptedBvn())).thenReturn(null);
//        when(userIdentityOutputPort.findById(testId)).thenReturn(favour);
        favour.setIdentityVerified(false);

        PremblyNinResponse premblyNinResponse = new PremblyNinResponse();
        premblyNinResponse.setVerification(Verification.builder().status("VERIFIED").build());
        premblyNinResponse.setNinData(PremblyNinResponse.NinData.builder().gender("m").build());
        premblyNinResponse.setFaceData(PremblyFaceData.builder().faceVerified(true).build());

        when(identityVerificationOutputPort.verifyNinLikeness(identityVerification)).thenReturn(premblyNinResponse);

        premblyNinResponse.setLikenessCheckSuccessful(true);

        PremblyResponse premblyResponse = new PremblyBvnResponse();
        premblyResponse.setVerification(Verification.builder().status("VERIFIED").build());
        PremblyBvnResponse premblyBvnResponse = new PremblyBvnResponse();
        premblyBvnResponse.setVerification(Verification.builder().status("VERIFIED").build());
        premblyBvnResponse.setData(PremblyBvnResponse.BvnData.builder().
                faceData(PremblyFaceData.builder().faceVerified(true).build()).build());

        when(identityVerificationOutputPort.verifyBvnLikeness(identityVerification)).thenReturn(premblyBvnResponse);
        premblyBvnResponse.setLikenessCheckSuccessful(Boolean.TRUE);

        when(userIdentityOutputPort.findById(any())).thenReturn(favour);

        when(identityVerificationMapper.updateUserIdentity(premblyNinResponse.getNinData(),favour))
                .thenReturn(favour2);

        favour2.setIdentityVerified(true);
        when(userIdentityOutputPort.save(favour2)).thenReturn(favour2);

        when(identityManagerOutputPort.getUserById(favour2.getId())).thenReturn(favour2);
        when(identityManagerOutputPort.updateUserData(favour2)).thenReturn(favour2);




        when(loanReferralOutputPort.findAllLoanReferralsByUserIdAndStatus(favour.getId(), LoanReferralStatus.AUTHORIZED))
                .thenReturn(List.of(loanReferral));

        when(organizationIdentityOutputPort.findOrganizationByName(loanReferral.getCohortLoanee().getReferredBy())).
                thenReturn(Optional.ofNullable(organizationIdentity));
        when(loanMetricsOutputPort.findByOrganizationId(organizationIdentity.getId()))
                .thenReturn(Optional.ofNullable(loanMetrics));
        when(loanMetricsOutputPort.save(loanMetrics)).thenReturn(loanMetrics);

        when(cohortOutputPort.save(cohort)).thenReturn(cohort);

        when(cohortLoanDetailOutputPort.findByCohortId(cohort.getId())).thenReturn(cohortLoanDetail);
        when(cohortLoanDetailOutputPort.save(cohortLoanDetail)).thenReturn(cohortLoanDetail);
        when(programLoanDetailOutputPort.findByProgramId(cohort.getProgramId())).thenReturn(programLoanDetail);
        when(programLoanDetailOutputPort.save(programLoanDetail)).thenReturn(programLoanDetail);
        when(organizationLoanDetailOutputPort.findByOrganizationId(cohort.getOrganizationId())).thenReturn(organizationLoanDetail);
        when(organizationLoanDetailOutputPort.save(organizationLoanDetail)).thenReturn(organizationLoanDetail);





        String response = identityVerificationService.verifyIdentity(testId,identityVerification);
        assertEquals(IDENTITY_VERIFIED.getMessage(), response);
    }

    @Test
    void verifyIdentityWithInvalidBvn() {
        identityVerification.setEncryptedBvn(StringUtils.EMPTY);
        assertThrows(MeedlException.class, () -> identityVerificationService.verifyIdentity(testId,identityVerification));
    }

    @Test
    void verifyIdentityWithInvalidNin() {
        identityVerification.setEncryptedNin(StringUtils.SPACE);
        assertThrows(MeedlException.class, () -> identityVerificationService.verifyIdentity(testId,identityVerification));
    }

    @Test
    void verifyIdentityOfBlacklistedReferral() throws MeedlException {
        when(identityVerificationFailureRecordOutputPort.countByUserId(testId)).thenReturn(5L);
        assertThrows(IdentityException.class, () -> identityVerificationService.verifyIdentity(testId,identityVerification));
    }

    @Test
    void verifyIdentityFailedVerificationCreatesFailureRecord() throws MeedlException {
        PremblyResponse premblyResponse = new PremblyBvnResponse();
//        premblyResponse.setVerification(Verification.builder().status("VERIFIED").build());
        when(tokenUtils.decryptAES(testBvn, "Error processing identity verification")).thenReturn("12345678901");
        when(tokenUtils.decryptAES(testNin, "Error processing identity verification")).thenReturn("12345678901");
        when(userIdentityOutputPort.findByBvn(testBvn)).thenReturn(favour);
//        when(identityVerificationOutputPort.verifyBvn(identityVerification)).thenReturn(premblyResponse);
        PremblyNinResponse premblyBvnResponse = new PremblyNinResponse();
        premblyBvnResponse.setVerification(Verification.builder().status("NOT-VERIFIED").build());
        when(identityVerificationOutputPort.verifyNinLikeness(identityVerification)).thenReturn(
                premblyBvnResponse);
//        when(userIdentityOutputPort.findById(loanReferral.getLoanee().getUserIdentity().getId()
//        )).thenReturn(favour);
        String response = identityVerificationService.verifyIdentity(testId,identityVerification);
        assertEquals(IDENTITY_NOT_VERIFIED.getMessage(), response);
    }


    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "iurei"})
    void verifyUserIdentityVerifiedByInvalidLoanReferralId(String id) {
        assertThrows(MeedlException.class, ()-> identityVerificationService.verifyIdentity(id));
    }

    @Test
    void verifyUserIdentityVerifiedByLoanReferralId() {
        try {
            loanReferral.getLoanee().getUserIdentity().setIdentityVerified(Boolean.TRUE);
            when(loanReferralOutputPort.findLoanReferralById(testId)).thenReturn(Optional.ofNullable(loanReferral));
            String response = identityVerificationService.verifyIdentity(testId);
            assertEquals(IDENTITY_VERIFIED.getMessage(), response);
        } catch (MeedlException e) {
            log.error("Error while verifying user identity {}", e.getMessage());
        }
    }
    @Test
    void identityNotVerifiedForUnVerifiedUser() {
        try {
            loanReferral.getLoanee().getUserIdentity().setIdentityVerified(Boolean.FALSE);
            when(loanReferralOutputPort.findLoanReferralById(testId)).thenReturn(Optional.ofNullable(loanReferral));
            String response = identityVerificationService.verifyIdentity(testId);
            assertEquals(IDENTITY_NOT_VERIFIED.getMessage(), response);
        } catch (MeedlException e) {
            log.error("Error while verifying user identity {}", e.getMessage());
        }
    }
    @Test
    void failedVerificationBlackListed(){
        when(identityVerificationFailureRecordOutputPort.createIdentityVerificationFailureRecord(identityVerificationFailureRecord)).thenReturn(identityVerificationFailureRecord);
        when(identityVerificationFailureRecordOutputPort.countByUserId(identityVerificationFailureRecord.getUserId())).thenReturn(5L);

        assertThrows(IdentityException.class, ()->identityVerificationService.createIdentityVerificationFailureRecord(identityVerificationFailureRecord));
    }
    @Test
    void failedVerificationNotBlackListed() {
        when(identityVerificationFailureRecordOutputPort.createIdentityVerificationFailureRecord(identityVerificationFailureRecord)).thenReturn(identityVerificationFailureRecord);
        when(identityVerificationFailureRecordOutputPort.countByUserId(identityVerificationFailureRecord.getUserId())).thenReturn(4L);
        try {
            String response = identityVerificationService.createIdentityVerificationFailureRecord(identityVerificationFailureRecord);
            assertNotNull(response);
            assertEquals(IDENTITY_VERIFICATION_FAILURE_SAVED.getMessage(), response);
        } catch (IdentityException e) {
            log.error("Error creating identity verification failure record {}", e.getMessage());
        }
    }


}