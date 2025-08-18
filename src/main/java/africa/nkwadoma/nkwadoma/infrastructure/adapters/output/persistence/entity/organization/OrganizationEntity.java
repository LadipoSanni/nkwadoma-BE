package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
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
    private String registrationNumber;
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
    private int numberOfLoanees;
    private int stillInTraining;
    private int numberOfCohort;
    private boolean isEnabled;
    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private int numberOfPrograms;
    private String logoImage;
    private String bannerImage;
    private String address;
    private String officeAddress;
}
