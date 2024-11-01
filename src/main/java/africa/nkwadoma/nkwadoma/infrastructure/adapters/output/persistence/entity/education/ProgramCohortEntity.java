package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProgramCohortEntity{

    @Id
    @UuidGenerator
    private String id;
    @ManyToOne
    private CohortEntity cohort;
    private String program;


}
