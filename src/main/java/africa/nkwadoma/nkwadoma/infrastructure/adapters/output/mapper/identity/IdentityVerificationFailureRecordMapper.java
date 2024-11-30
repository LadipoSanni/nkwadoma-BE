package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity;

import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerificationFailureRecord;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.IdentityVerificationFailureRecordEntity;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IdentityVerificationFailureRecordMapper {
    IdentityVerificationFailureRecordEntity mapToVerificationFailureRecordEntity(IdentityVerificationFailureRecord record);

    @InheritConfiguration
    IdentityVerificationFailureRecord mapToIdentityVerificationFailureRecord(IdentityVerificationFailureRecordEntity verificationFailureRecordEntity);
}
