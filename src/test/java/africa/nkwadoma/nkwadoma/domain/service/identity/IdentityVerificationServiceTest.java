package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
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

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.IDENTITY_NOT_VERIFIED;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.IDENTITY_VERIFIED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private TokenUtils tokenUtils;
    private UserIdentity favour;
    private final String generatedToken = "generatedToken";
    private final String testBvn = "12345678956";
    private final String testId = "c508e3bb-1193-4fc7-aa75-e1335c78ef1e";
    private final String testNin = "21345678908";
    private IdentityVerification identityVerification;
    private IdentityVerificationEntity identityVerificationEntity;

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
        identityVerificationEntity.setId("9c558b64-c207-4c34-99c7-8d2f04398496");
        identityVerificationEntity.setBvn(testBvn);
        identityVerificationEntity.setNin(testNin);

        identityVerification = new IdentityVerification();
        identityVerification.setBvn(testBvn);
        identityVerification.setNin(testNin);
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
        } catch (MeedlException | IdentityVerificationException e) {
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
        } catch (MeedlException | IdentityVerificationException e) {
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
        } catch (MeedlException|IdentityVerificationException e) {
            log.error("Verification failed : {}", e.getMessage());
        }
    }

}