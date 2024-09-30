package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity;

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
//    @ManyToOne
//    private OrganizationEntity organization;
    @ManyToOne
    private UserEntity middlUser;

    private String organization;


//    private UserEntity middlUser;
//
//    private String organization;
//    private String middlUser;

}
