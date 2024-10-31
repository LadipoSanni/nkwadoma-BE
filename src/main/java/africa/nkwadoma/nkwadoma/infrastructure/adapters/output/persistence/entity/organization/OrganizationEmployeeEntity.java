package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "organization_employee", uniqueConstraints = {@UniqueConstraint(columnNames = {"organization", "middl_user_id"},
        name = "uk_organization_employee")})
public class OrganizationEmployeeEntity {
    @Id
    @UuidGenerator
    private String id;
    @ManyToOne
    private UserEntity meedlUser;
    private String organization;


}
