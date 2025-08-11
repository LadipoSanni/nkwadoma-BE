package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "organization_employee", uniqueConstraints = {@UniqueConstraint(columnNames = {"organization", "meedl_user_id"},
        name = "uk_organization_employee")})
public class OrganizationEmployeeEntity {
    @Id
    @UuidGenerator
    private String id;
    @ManyToOne
    private UserEntity meedlUser;
    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus;
    private String organization;
}
