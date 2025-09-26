package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.IdentityVerificationStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity
public class IdentityVerificationEntity {
    @UuidGenerator
    @Id
    private String id;
    private String bvn;
    private String nin;
    private String referralId;
    private String email;
    private IdentityVerificationStatus status;
}
