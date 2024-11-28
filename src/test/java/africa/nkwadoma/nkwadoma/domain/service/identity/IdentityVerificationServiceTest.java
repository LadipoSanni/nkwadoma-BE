package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationFailureRecordOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.ServiceProvider;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerificationFailureRecord;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.IdentityVerificationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.IdentityVerificationMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.IdentityVerificationStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity.IdentityVerificationRepository;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.IdentityVerificationException;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.TokenUtils;
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
    private IdentityVerificationRepository identityVerificationRepository;
    @Mock
    private IdentityVerificationMapper identityVerificationMapper;
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private IdentityVerificationFailureRecordOutputPort identityVerificationFailureRecordOutputPort;
    @Mock
    private TokenUtils tokenUtils;
    private UserIdentity favour;
    private String testId ="9c558b64-c207-4c34-99c7-8d2f04398496";
    private String testBvn = "12345678956";
    private String testNin = "21345678908";
    private final String generatedToken = "generatedToken";
    private IdentityVerification identityVerification;
    private IdentityVerificationEntity identityVerificationEntity;
    private IdentityVerificationFailureRecord identityVerificationFailureRecord;

    @BeforeEach
    void setUp() {
        favour = new UserIdentity();
        favour.setFirstName("favour");
        favour.setLastName("gabriel");
        favour.setPassword("Passkey90@");
        favour.setEmail("favour@gmail.com");
        favour.setRole(IdentityRole.ORGANIZATION_ADMIN);
        favour.setId(testId);
        favour.setReactivationReason("Reason for reactivation is to test");
        favour.setDeactivationReason("Reason for deactivation is to test");

        identityVerificationEntity = new IdentityVerificationEntity();
        identityVerificationEntity.setId(testId);
        identityVerificationEntity.setBvn(testBvn);
        identityVerificationEntity.setNin(testNin);

        identityVerification = new IdentityVerification();
        identityVerification.setBvn(testBvn);
        identityVerification.setNin(testNin);

        identityVerificationFailureRecord = IdentityVerificationFailureRecord.builder()
                .email("test@example.com")
                .reason("wrong bvn")
                .referralId(testId)
                .serviceProvider(ServiceProvider.SMILEID)
                .build();
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "iurei"})
    void verifyUserIdentityVerifiedByInvalidEmail(String token) {
        assertThrows(MeedlException.class, ()-> identityVerificationService.isIdentityVerified(token));
    }

    @Test
    void verifyUserIdentityVerifiedByEmail() {
        try {
            when(identityVerificationRepository.findByEmailAndStatus(favour.getEmail(), IdentityVerificationStatus.VERIFIED)).thenReturn(Optional.of(identityVerificationEntity));
            when(tokenUtils.decodeJWTGetEmail(generatedToken)).thenReturn(favour.getEmail());
            assertEquals(IDENTITY_VERIFIED.getMessage(), identityVerificationService.isIdentityVerified(generatedToken));
        } catch (MeedlException e) {
            log.error("Error while verifying user identity {}", e.getMessage());
        }
    }
    @Test
    void verifyNonExistingUserIdentityIsVerifiedByEmail() {
        try {
            when(identityVerificationRepository.findByEmailAndStatus(favour.getEmail(), IdentityVerificationStatus.VERIFIED)).thenReturn(Optional.empty());
            when(identityVerificationRepository.countByReferralId(testId)).thenReturn(1L);
            when(tokenUtils.decodeJWTGetEmail(generatedToken)).thenReturn(favour.getEmail());
            when(tokenUtils.decodeJWTGetId(generatedToken)).thenReturn(testId);
            String response = identityVerificationService.isIdentityVerified(generatedToken);
            assertEquals(IDENTITY_NOT_VERIFIED.getMessage(), response);
        } catch (MeedlException e) {
            log.error("Error while verifying user identity {}", e.getMessage());
        }
    }
    @Test
    void verificationBeyondThreshold() {
        try {
            when(identityVerificationRepository.findByEmailAndStatus(favour.getEmail(), IdentityVerificationStatus.VERIFIED)).thenReturn(Optional.empty());
            when(identityVerificationRepository.countByReferralId(testId)).thenReturn(6L);
            when(tokenUtils.decodeJWTGetEmail(generatedToken)).thenReturn(favour.getEmail());
            when(tokenUtils.decodeJWTGetId(generatedToken)).thenReturn(testId);
            assertThrows(IdentityVerificationException.class, ()-> identityVerificationService.isIdentityVerified(generatedToken));
        } catch (MeedlException  e) {
            log.error("Error while verifying user identity {}", e.getMessage());
        }
    }
    @Test
    void verifyIdentityWithoutBvnOrNin(){
        identityVerification.setBvn(null);
        identityVerification.setNin(null);
        assertThrows(MeedlException.class, ()-> identityVerificationService.isIdentityVerified(identityVerification));
    }
    @Test
    void verifyIdentityWithAtLeastOneIdentifier(){
        identityVerification.setBvn(null);
        identityVerification.setNin(testNin);
        assertThrows(MeedlException.class, ()-> identityVerificationService.isIdentityVerified(identityVerification));
    }
    @Test
    void verifyUserBvn(){
        when(identityVerificationRepository.findByBvnAndStatus(testBvn, IdentityVerificationStatus.VERIFIED)).thenReturn(Optional.of(identityVerificationEntity));
        try {
            String response = identityVerificationService.isIdentityVerified(identityVerification);
            assertNotNull(response);
            assertEquals(IDENTITY_VERIFIED.getMessage(), response);
        } catch (MeedlException e) {
            log.error("Verification failed : {}", e.getMessage());
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