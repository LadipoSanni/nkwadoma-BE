package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity;

import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import jakarta.persistence.*;
import lombok.*;


@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "meedl_user")
public class UserEntity {
    @Id
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean emailVerified;
    private boolean enabled;
    private String createdAt;
    @Enumerated(EnumType.STRING)
    private IdentityRole role;
    private String alternateEmail;
    private String alternatePhoneNumber;
    private String alternateContactAddress;
    private String createdBy;
    private String reactivationReason;
    private String deactivationReason;
    private String bvn;
}
