package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FinancierType;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity
public class FinancierEntity {

    @Id
    @UuidGenerator
    private String id;
    private String organizationName;
    @OneToOne
    private OrganizationEntity organizationEntity;
    @OneToOne
    private UserEntity userIdentity;
    private String invitedBy;
    @Enumerated(EnumType.STRING)
    private FinancierType financierType;
    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus;
}
