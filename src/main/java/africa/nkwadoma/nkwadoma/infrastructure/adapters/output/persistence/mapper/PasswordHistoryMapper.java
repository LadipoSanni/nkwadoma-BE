package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.identity.PasswordHistory;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.PasswordHistoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PasswordHistoryMapper {
    PasswordHistoryEntity toPasswordHistoryEntity(PasswordHistory passwordHistory);
    PasswordHistory toPasswordHistory(PasswordHistoryEntity passwordHistoryEntity);
}
