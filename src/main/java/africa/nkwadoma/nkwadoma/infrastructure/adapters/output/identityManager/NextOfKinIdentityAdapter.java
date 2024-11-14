package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.springframework.stereotype.*;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class NextOfKinIdentityAdapter implements NextOfKinIdentityOutputPort {
    private final NextOfKinRepository nextOfKinRepository;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final NextOfKinMapper nextOfKinMapper;
    private final UserIdentityMapper userIdentityMapper;

    @Override
    public NextOfKin save(NextOfKin nextOfKin) throws MeedlException {
        if (ObjectUtils.isEmpty(nextOfKin)) {
            throw new IdentityException(IdentityMessages.NEXT_OF_KIN_CANNOT_BE_NULL.getMessage());
        }
        MeedlValidator.validateObjectInstance(nextOfKin.getLoanee().getUserIdentity());
        updateUserEntity(nextOfKin);
        NextOfKinEntity nextOfKinEntity = nextOfKinMapper.toNextOfKinEntity(nextOfKin);
        NextOfKinEntity savedNextOfKinEntity = nextOfKinRepository.save(nextOfKinEntity);
        return nextOfKinMapper.toNextOfKin(savedNextOfKinEntity);
    }

    private void updateUserEntity(NextOfKin nextOfKin) throws MeedlException {
        UserIdentity userIdentity = userIdentityOutputPort.findById(nextOfKin.getLoanee().getUserIdentity().getId());
        if (ObjectUtils.isNotEmpty(userIdentity)) {
            UserEntity userEntity = userIdentityMapper.toUserEntity(userIdentity);
            UserEntity updateUserEntity = userIdentityMapper.updateUserEntity(userEntity, nextOfKin.getLoanee().getUserIdentity());
            userIdentity = userIdentityMapper.toUserIdentity(updateUserEntity);
            userIdentityOutputPort.save(userIdentity);
        }
    }

    @Override
    public void deleteNextOfKin(String nextOfKinId) throws MeedlException {
        MeedlValidator.validateUUID(nextOfKinId);
        Optional<NextOfKinEntity> foundNextOfKin = nextOfKinRepository.findById(nextOfKinId);
        if (foundNextOfKin.isPresent()) {
            nextOfKinRepository.delete(foundNextOfKin.get());
        }
    }

    @Override
    public NextOfKin findByEmail(String email) throws MeedlException {
        MeedlValidator.validateEmail(email);
        Optional<NextOfKinEntity> foundNextOfKin = nextOfKinRepository.findByEmail(email);
        NextOfKin nextOfKin = null;
        if (foundNextOfKin.isPresent()) {
            nextOfKin = nextOfKinMapper.toNextOfKin(foundNextOfKin.get());
        }
        return nextOfKin;
    }
}
