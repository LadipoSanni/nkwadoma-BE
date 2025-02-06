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
    private String image;
    private String phoneNumber;
    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isIdentityVerified;
    private boolean emailVerified;
    private boolean enabled;
    private String lgaOfOrigin;
    private String middleName;
    private String State;
    private String lgaOfResidence;
    private String createdAt;
    @Enumerated(EnumType.STRING)
    private IdentityRole role;
    private String gender;
    private String dateOfBirth;
    private String stateOfOrigin;
    private String maritalStatus;
    private String stateOfResidence;
    private String nationality;
    private String residentialAddress;
    private String alternateEmail;
    private String alternatePhoneNumber;
    private String alternateContactAddress;
    private String createdBy;
    private String reactivationReason;
    private String deactivationReason;
    private String bvn;
    private String nin;
}
