package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education;

import africa.nkwadoma.nkwadoma.domain.enums.DurationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @OneToOne
    @JoinColumn(name = "curriculum_entity_id")
    private CurriculumEntity curriculumEntity;
    @ManyToOne
    private TrainingInstituteEntity trainingInstituteEntity;
}
