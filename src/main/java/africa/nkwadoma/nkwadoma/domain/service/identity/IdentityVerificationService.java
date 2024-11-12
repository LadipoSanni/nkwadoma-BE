package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.VerificationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.InvalidInputException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager.PremblyAdapter;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.IdentityVerificationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.IdentityVerificationMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity.IdentityVerificationRepository;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.*;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.IDENTITY_NOT_VERIFIED;

@Slf4j
@RequiredArgsConstructor
@Service
public class IdentityVerificationService implements VerificationUseCase {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final IdentityVerificationRepository identityVerificationRepository;
    private final IdentityVerificationMapper identityVerificationMapper;
    private final TokenUtils tokenUtils;
    private final PremblyAdapter premblyAdapter;

    @Override
    public String verifyIdentity(String token) throws MeedlException {
        String email = tokenUtils.decodeJWT(token);
        MeedlValidator.validateEmail(email);
        UserIdentity foundUser = userIdentityOutputPort.findByEmail(email);
        boolean identityVerified = isIdentityVerified(foundUser);
        if (identityVerified) {
            log.info(USER_EMAIL_PREVIOUSLY_VERIFICATION.format(email, identityVerified));
            return IDENTITY_VERIFIED.getMessage();
        }
        log.info(USER_EMAIL_NOT_PREVIOUSLY_VERIFICATION.format(email, identityVerified));
        return IDENTITY_NOT_VERIFIED.getMessage();
    }
    @Override
    public IdentityVerification verifyIdentity(IdentityVerification identityVerification) throws MeedlException {
        IdentityVerification foundIdentityVerification = findByBvnOrNin(identityVerification);
        
        return identityVerification;
    }

    private IdentityVerification findByBvnOrNin(IdentityVerification identityVerification) throws MeedlException {
        identityVerification.validate();
        MeedlValidator.validateDataElement(identityVerification.getBvn());
        IdentityVerificationEntity identityVerificationEntity = identityVerificationRepository.findByBvn(identityVerification.getBvn());
        return identityVerificationMapper.mapToIdentityVerification(identityVerificationEntity);
    }

    private boolean isIdentityVerified(UserIdentity foundUser){
        return true;
    }

}
