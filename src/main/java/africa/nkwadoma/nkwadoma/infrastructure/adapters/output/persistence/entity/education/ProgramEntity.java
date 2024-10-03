package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgramEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String programDescription;
    private String name;
    private String type;
    private String objectives;
    private DurationStatus durationStatus;
    private int duration;
    private int numberOfTrainees;
    private int numberOfCohort;
    @Enumerated(EnumType.STRING)
    private ProgramMode mode;
    @Enumerated(EnumType.STRING)
    private ProgramType programType;
    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;
    @Enumerated(EnumType.STRING)
    private ProgramStatus programStatus;
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
    @OneToOne
    @JoinColumn(name = "curriculum_entity_id")
    private CurriculumEntity curriculumEntity;
    @ManyToOne
    private TrainingInstituteEntity trainingInstituteEntity;
}
