package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity;

import africa.nkwadoma.nkwadoma.domain.enums.ServiceProvider;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity
public class IdentityVerificationFailureRecordEntity {
    @Id
    @UuidGenerator
    private String id;
        private String email;
        private String reason;
        private String referralId;
        private ServiceProvider serviceProvider;
}
