package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper;

import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerificationFailureRecord;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.IdentityVerificationFailureRecordRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.IdentityVerificationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = DurationTypeMapper.class)
public interface IdentityVerificationRestMapper {

    IdentityVerification toIdentityVerification(IdentityVerificationRequest identityVerificationRequest);

    IdentityVerificationFailureRecord toIdentityVerificationFailureRecord(IdentityVerificationFailureRecordRequest identityVerificationFailureRecordRequest);
}
