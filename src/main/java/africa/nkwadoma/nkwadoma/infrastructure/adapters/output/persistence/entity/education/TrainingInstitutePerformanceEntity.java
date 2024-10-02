package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education;

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
public class TrainingInstitutePerformanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private double overallEnrolmentRate;
    private double overallSuccessRate;
    private double totalRevenueGenerated;
    @OneToOne
    private TrainingInstituteEntity trainingInstituteEntity;
}
