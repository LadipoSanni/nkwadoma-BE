package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "subject_entity")
public class SubjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String description;
    @ManyToOne
    private CurriculumEntity curriculumEntity;
}