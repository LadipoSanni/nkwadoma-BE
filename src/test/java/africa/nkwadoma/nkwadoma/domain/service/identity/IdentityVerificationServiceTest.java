package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.output.aes.AesOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationFailureRecordOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanReferralOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ServiceProvider;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerificationFailureRecord;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
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

import java.util.Optional;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.IDENTITY_VERIFICATION_FAILURE_SAVED;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.IDENTITY_NOT_VERIFIED;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.IDENTITY_VERIFIED;
import static org.junit.jupiter.api.Assertions.*;
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
    private UserIdentity favour;
    private LoanReferral loanReferral;
    private final String testId ="9c558b64-c207-4c34-99c7-8d2f04398496";
    private final String testBvn = "etlGGJ4BSGNxBkqfv3rPqw==";
    private final String testNin = "etlGGJ4BSGNxBkqfv3rPqw==";
    private IdentityVerification identityVerification;
    private IdentityVerificationFailureRecord identityVerificationFailureRecord;

    @BeforeEach
    void setUp() {
        favour = TestData.createTestUserIdentity("favour@gmail.com");
        Loanee loanee = TestData.createTestLoanee(favour, TestData.createTestLoaneeLoanDetail());
        loanee.setUserIdentity(favour);
        loanReferral = LoanReferral.builder().loanee(loanee).build();

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

//    @Test
//    void verifyIdentitySuccessfulVerification() throws MeedlException {
//        when(tokenUtils.decryptAES(testBvn)).thenReturn("12345678901");
//        when(tokenUtils.decryptAES(testNin)).thenReturn("12345678901");
//        when(loanReferralOutputPort.findById(identityVerification.getLoanReferralId())).thenReturn(loanReferral);
//        favour.setIdentityVerified(Boolean.TRUE);
//        when(userIdentityOutputPort.findByBvn(identityVerification.getEncryptedBvn())).thenReturn(favour);
//        when(userIdentityOutputPort.findById(favour.getId())).thenReturn(favour);
//        PremblyResponse premblyResponse = new PremblyBvnResponse();
//        premblyResponse.setVerification(Verification.builder().status("VERIFIED").build());
//        PremblyBvnResponse premblyBvnResponse = new PremblyBvnResponse();
//        premblyBvnResponse.setVerification(Verification.builder().status("VERIFIED").build());
//        when(identityVerificationOutputPort.verifyBvnLikeness(identityVerification)).thenReturn(premblyBvnResponse);
//        favour.setIdentityVerified(false);
//
//        String response = identityVerificationService.verifyIdentity(identityVerification);
//        assertEquals(IDENTITY_VERIFIED.getMessage(), response);
//    }

    @Test
    void verifyIdentityWithInvalidBvn() {
        identityVerification.setDecryptedBvn(StringUtils.EMPTY);
        assertThrows(MeedlException.class, () -> identityVerificationService.verifyIdentity(testId,identityVerification));
    }

    @Test
    void verifyIdentityWithInvalidNin() {
        identityVerification.setDecryptedNin(StringUtils.SPACE);
        assertThrows(MeedlException.class, () -> identityVerificationService.verifyIdentity(testId,identityVerification));
    }

    @Test
    void verifyIdentityOfBlacklistedReferral() throws MeedlException {
        when(loanReferralOutputPort.findLoanReferralById(identityVerification.getLoanReferralId())).thenReturn(Optional.of(loanReferral));
        when(identityVerificationFailureRecordOutputPort.countByUserId(loanReferral.getId())).thenReturn(5L);
        assertThrows(IdentityException.class, () -> identityVerificationService.verifyIdentity(testId,identityVerification));
    }

    @Test
    void verifyIdentityFailedVerificationCreatesFailureRecord() throws MeedlException {
        PremblyResponse premblyResponse = new PremblyBvnResponse();
//        premblyResponse.setVerification(Verification.builder().status("VERIFIED").build());
        when(tokenUtils.decryptAES(testBvn, "Error processing identity verification")).thenReturn("12345678901");
        when(tokenUtils.decryptAES(testNin, "Error processing identity verification")).thenReturn("12345678901");
        when(loanReferralOutputPort.findLoanReferralById(identityVerification.getLoanReferralId())).thenReturn(Optional.of(loanReferral));
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