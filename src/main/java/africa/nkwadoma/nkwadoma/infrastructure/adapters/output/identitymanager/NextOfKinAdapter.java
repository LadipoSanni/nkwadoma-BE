package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identitymanager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class NextOfKinAdapter implements NextOfKinOutputPort {
    private final NextOfKinRepository nextOfKinRepository;
    private final NextOfKinMapper nextOfKinMapper;

    @Override
    public NextOfKin save(NextOfKin nextOfKin) throws MeedlException {
        MeedlValidator.validateObjectInstance(nextOfKin, IdentityMessages.NEXT_OF_KIN_CANNOT_BE_NULL.getMessage());
        nextOfKin.validate();
        NextOfKinEntity nextOfKinEntity = nextOfKinMapper.toNextOfKinEntity(nextOfKin);
        NextOfKinEntity savedNextOfKinEntity = nextOfKinRepository.save(nextOfKinEntity);
        return nextOfKinMapper.toNextOfKin(savedNextOfKinEntity);
    }



    @Override
    public void deleteNextOfKin(String nextOfKinId) throws MeedlException {
        MeedlValidator.validateUUID(nextOfKinId, "Please provide a valid next of kin identification.");
        Optional<NextOfKinEntity> foundNextOfKin = nextOfKinRepository.findById(nextOfKinId);
        foundNextOfKin.ifPresent(nextOfKinRepository::delete);
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

//    @Override
//    public Optional<NextOfKin> findByUserId(String userId) throws MeedlException {
//        MeedlValidator.validateUUID(userId, UserMessages.INVALID_USER_ID.getMessage());
//        Optional<NextOfKinEntity> nextOfKinEntity = nextOfKinRepository.findByUserId(userId);
//        log.info("Found nextOfKin optional {}", nextOfKinEntity.isPresent());
//        return nextOfKinEntity.map(nextOfKinMapper::toNextOfKin);
//    }
}
