package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.OrganizationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

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
    @NotBlank
    private String name;
    @NotBlank
    private String email;
    private String websiteAddress;
    private LocalDateTime invitedDate;
    private LocalDateTime requestedInvitationDate;
//    private String registrationNumber;
    @Column(unique = true)
    private String taxIdentity;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus;
    @Column(unique = true)
    private String rcNumber;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime timeUpdated;
    private boolean isEnabled;
    private String logoImage;
    private String bannerImage;
    private String address;
    private String officeAddress;
    @Enumerated(EnumType.STRING)
    private OrganizationType organizationType;
}
