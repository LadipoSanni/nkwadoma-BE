package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.PasswordHistoryOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.PasswordHistory;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.OrganizationEmployeeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.PasswordHistoryEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.PasswordHistoryMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.PasswordHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.*;

@RequiredArgsConstructor
public class PasswordHistoryAdapter implements PasswordHistoryOutputPort {
    private final PasswordHistoryMapper passwordHistoryMapper;
    private final PasswordHistoryRepository passwordHistoryRepository;
    @Override
    public PasswordHistory save(PasswordHistory passwordHistory) {
        PasswordHistoryEntity passwordHistoryEntity = passwordHistoryMapper.toPasswordHistoryEntity(passwordHistory);
        passwordHistoryEntity = passwordHistoryRepository.save(passwordHistoryEntity);
        return passwordHistoryMapper.toPasswordHistory(passwordHistoryEntity);
    }

    @Override
    public List<PasswordHistory> findByUser(String id) throws MiddlException {
        List<PasswordHistory> passwordHistories = new ArrayList<>();
        if(!StringUtils.isEmpty(id)){
            List<PasswordHistoryEntity> passwordHistoryEntities = passwordHistoryRepository.findByMiddlUser(id);
            if (passwordHistoryEntities == null){
                throw new IdentityException(PASSWORD_HISTORY_EMPTY.getMessage());
            }

            for (PasswordHistoryEntity passwordHistoryEntity : passwordHistoryEntities){
               PasswordHistory passwordHistory = passwordHistoryMapper.toPasswordHistory(passwordHistoryEntity);
               passwordHistories.add(passwordHistory);
            }

            return passwordHistories;
        }
        throw new IdentityException(USER_IDENTITY_CANNOT_BE_NULL.getMessage());
    }
}
