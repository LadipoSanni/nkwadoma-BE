package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationFailureRecordOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanReferralOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ServiceProvider;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerificationFailureRecord;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.IdentityVerificationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.IdentityVerificationException;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.TokenUtils;
import africa.nkwadoma.nkwadoma.test.data.TestData;
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
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private IdentityVerificationFailureRecordOutputPort identityVerificationFailureRecordOutputPort;
    @Mock
    private TokenUtils tokenUtils;
    private UserIdentity favour;
    private LoanReferral loanReferral;
    private final String testId ="9c558b64-c207-4c34-99c7-8d2f04398496";
    private final String testBvn = "etlGGJ4BSGNxBkqfv3rPqw==";
    private final String testNin = "etlGGJ4BSGNxBkqfv3rPqw==";
    private IdentityVerification identityVerification;
    private IdentityVerificationEntity identityVerificationEntity;
    private IdentityVerificationFailureRecord identityVerificationFailureRecord;

    @BeforeEach
    void setUp() {
        favour = TestData.createTestUserIdentity("favour@gmail.com");
        Loanee loanee = TestData.createTestLoanee(favour, TestData.createTestLoaneeLoanDetail());
        loanee.setUserIdentity(favour);
        loanReferral = LoanReferral.builder().loanee(loanee).build();

        identityVerificationEntity = new IdentityVerificationEntity();
        identityVerificationEntity.setId(testId);
        identityVerificationEntity.setBvn(testBvn);
        identityVerificationEntity.setNin(testNin);

        identityVerification = new IdentityVerification();
        identityVerification.setEncryptedBvn(testBvn);
        identityVerification.setEncryptedNin(testNin);

        identityVerificationFailureRecord = IdentityVerificationFailureRecord.builder()
                .email("test@example.com")
                .reason("wrong bvn")
                .referralId(testId)
                .serviceProvider(ServiceProvider.SMILEID)
                .build();
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
        when(identityVerificationFailureRecordOutputPort.countByReferralId(identityVerificationFailureRecord.getReferralId())).thenReturn(5L);

        assertThrows(IdentityVerificationException.class, ()->identityVerificationService.createIdentityVerificationFailureRecord(identityVerificationFailureRecord));
    }
    @Test
    void failedVerificationNotBlackListed() {
        when(identityVerificationFailureRecordOutputPort.createIdentityVerificationFailureRecord(identityVerificationFailureRecord)).thenReturn(identityVerificationFailureRecord);
        when(identityVerificationFailureRecordOutputPort.countByReferralId(identityVerificationFailureRecord.getReferralId())).thenReturn(4L);
        try {
            String response = identityVerificationService.createIdentityVerificationFailureRecord(identityVerificationFailureRecord);
            assertNotNull(response);
            assertEquals(IDENTITY_VERIFICATION_FAILURE_SAVED.getMessage(), response);
        } catch (IdentityVerificationException e) {
            log.error("Error creating identity verification failure record {}", e.getMessage());
        }
    }


}