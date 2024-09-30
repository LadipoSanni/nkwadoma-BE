package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "organization")
public class OrganizationEntity {
    @Id
    private String id;
    private String industry;
    private String name;
    private String email;
    private String websiteAddress;
    private String invitedDate;
    private String registrationNumber;
    private String taxIdentity;
    private String phoneNumber;
//    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
//    private List<UserEntity> organizationAdmins;
}
