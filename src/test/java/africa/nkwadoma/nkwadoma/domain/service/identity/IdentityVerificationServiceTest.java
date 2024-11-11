package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
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
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private TokenUtils tokenUtils;
    private final String generatedToken = "generatedToken";
    private UserIdentity favour;

    @BeforeEach
    void setUp() {
        favour = new UserIdentity();
        favour.setFirstName("favour");
        favour.setLastName("gabriel");
        favour.setPassword("Passkey90@");
        favour.setEmail("favour@gmail.com");
        favour.setRole(IdentityRole.ORGANIZATION_ADMIN);
        favour.setId("c508e3bb-1193-4fc7-aa75-e1335c78ef1e");
        favour.setReactivationReason("Reason for reactivation is to test");
        favour.setDeactivationReason("Reason for deactivation is to test");
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "iurei"})
    void verifyUserIdentityVerifiedByInvalidEmail(String token) {
        assertThrows(MeedlException.class, ()-> identityVerificationService.verifyByEmailUserIdentityVerified(token));
    }

    @Test
    void verifyUserIdentityVerifiedByEmail() {
        try {
            when(userIdentityOutputPort.findByEmail(any())).thenReturn(favour);
            when(tokenUtils.decodeJWT(generatedToken)).thenReturn(favour.getEmail());
            assertEquals(IDENTITY_VERIFIED.getMessage(), identityVerificationService.verifyByEmailUserIdentityVerified(generatedToken));
        } catch (MeedlException e) {
            log.error("Error while verifying user identity {}", e.getMessage());
        }
    }
    @Test
    void verifyNonExistingUserIdentityIsVerifiedByEmail() {
        try {
            when(userIdentityOutputPort.findByEmail(any())).thenThrow(MeedlException.class);
            when(tokenUtils.decodeJWT(generatedToken)).thenReturn(favour.getEmail());
            assertThrows(MeedlException.class, ()-> identityVerificationService.verifyByEmailUserIdentityVerified(generatedToken));
        } catch (MeedlException e) {
            log.error("Error while verifying user identity {}", e.getMessage());
        }
    }
    @Test
    void verifyUserIdentityNotVerifiedByEmail() {
//        try {
//            when(userIdentityOutputPort.findByEmail(any())).thenReturn(favour);
//            when(tokenUtils.decodeJWT(generatedToken)).thenReturn(favour.getEmail());
//            when(identityVerificationOutputPort.isIdentityVerified(favour)).thenReturn(false);
//            assertEquals(IDENTITY_NOT_VERIFIED.getMessage(), identityVerificationService.verifyByEmailUserIdentityVerified(generatedToken));
//        } catch (MeedlException e) {
//            log.error("Error while verifying user identity {}", e.getMessage());
//        }
    }
}