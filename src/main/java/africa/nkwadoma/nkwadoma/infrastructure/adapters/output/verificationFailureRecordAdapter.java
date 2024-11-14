package africa.nkwadoma.nkwadoma.infrastructure.adapters.output;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.VerificationFailureRecordOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerificationFailureRecord;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.IdentityVerificationFailureRecordMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.IdentityVerificationFailureRecordEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity.IdentityVerificationFailureRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class verificationFailureRecordAdapter implements VerificationFailureRecordOutputPort {
    private final IdentityVerificationFailureRecordRepository verificationFailureRecordRepository;
    private final IdentityVerificationFailureRecordMapper identityVerificationFailureRecordMapper;

    @Override
    public IdentityVerificationFailureRecord createIdentityVerificationFailureRecord(IdentityVerificationFailureRecord record) {
        IdentityVerificationFailureRecordEntity identityVerificationFailureRecordEntity = identityVerificationFailureRecordMapper.mapToVerificationFailureRecordEntity(record);
        identityVerificationFailureRecordEntity = verificationFailureRecordRepository.save(identityVerificationFailureRecordEntity);
        return identityVerificationFailureRecordMapper.mapToIdentityVerificationFailureRecord(identityVerificationFailureRecordEntity);
    }
}
