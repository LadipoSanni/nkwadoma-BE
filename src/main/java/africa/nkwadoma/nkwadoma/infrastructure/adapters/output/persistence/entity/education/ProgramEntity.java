package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(length = 2500)
    @Size(max = 2500, message = "Program description must not exceed 2500 characters")
    private String programDescription;
    private String name;
    @Lob
    private String objectives;
    @Enumerated(EnumType.STRING)
    private DurationType durationType;
    private int duration;
    private int numberOfLoanees;
    private int numberOfCohort;
    @Enumerated(EnumType.STRING)
    private ProgramMode mode;
    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;
    @Enumerated(EnumType.STRING)
    private ActivationStatus programStatus;
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDate programStartDate;
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    @Column(nullable = false)
    private String createdBy;
    private String updatedBy;
    @ManyToOne(cascade = CascadeType.MERGE)
    private OrganizationEntity organizationIdentity;
}