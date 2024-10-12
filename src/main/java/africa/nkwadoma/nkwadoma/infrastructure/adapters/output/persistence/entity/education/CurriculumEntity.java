package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "curriculum_entity")
public class CurriculumEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String description;
    @Lob
    private String objectives;
    private String duration;
    private String name;
    @OneToOne
    private ProgramEntity programEntity;
}