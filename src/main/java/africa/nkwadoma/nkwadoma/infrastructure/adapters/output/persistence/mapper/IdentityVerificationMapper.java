package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.IdentityVerificationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IdentityVerificationMapper {
    public IdentityVerification mapToIdentityVerification(IdentityVerificationEntity identityVerificationEntity);

    IdentityVerificationEntity mapToIdentityVerificationEntity(IdentityVerification identityVerificationEntity);
}
