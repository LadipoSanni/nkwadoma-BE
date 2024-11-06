package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.util.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Table(name = "organization")
public class OrganizationEntity {
    @Id
    @UuidGenerator
    private String id;
    private String name;
    private String email;
    private String websiteAddress;
    private String invitedDate;
    private String registrationNumber;
    private String taxIdentity;
    private String phoneNumber;
    private String rcNumber;
    private String createdBy;
    private boolean isEnabled;
    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private int numberOfPrograms;
//    private List<String> serviceOfferings = new ArrayList<>();


//    @OneToOne(cascade = {CascadeType.REMOVE}, orphanRemoval = true)
//    private ServiceOfferingEntity serviceOfferingEntity;
}
