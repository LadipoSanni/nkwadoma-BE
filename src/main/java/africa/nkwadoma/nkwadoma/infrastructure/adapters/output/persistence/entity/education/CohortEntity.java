package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CohortEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
}
