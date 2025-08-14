package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

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
    private String organization;
    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus;
    @ManyToOne
    private UserEntity meedlUser;
    private String createdBy;
}
