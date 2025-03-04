package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
@Getter
@Setter
@Entity
public class FinancierEntity {

    @Id
    @UuidGenerator
    private String id;
    @OneToMany
    private List<OrganizationEntity> organizations;
    @OneToMany
    private List<UserEntity> individuals;
    private String invitedBy;
}
