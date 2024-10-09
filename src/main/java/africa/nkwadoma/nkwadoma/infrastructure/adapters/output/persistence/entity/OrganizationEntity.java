package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity;
import africa.nkwadoma.nkwadoma.domain.enums.*;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String industry;
    private String name;
    private String email;
    private String websiteAddress;
    private String invitedDate;
    private String registrationNumber;
    @Enumerated(EnumType.STRING)
    private ServiceOffering serviceOffering;
    @Enumerated(EnumType.STRING)
    private IndustryType industryType;
    private String taxIdentity;
    private String phoneNumber;
//    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
//    private List<UserEntity> organizationAdmins;
}
