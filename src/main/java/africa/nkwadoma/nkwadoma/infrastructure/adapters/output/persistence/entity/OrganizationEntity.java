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
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String industry;
    private String name;
    private String email;
    private String websiteAddress;
    private String invitedDate;
    private String registrationNumber;
    private String taxIdentity;
    private String phoneNumber;
    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private int numberOfPrograms;
    @OneToOne(cascade = {CascadeType.REMOVE}, orphanRemoval = true)
    private ServiceOfferingEntity serviceOfferingEntity;
}
