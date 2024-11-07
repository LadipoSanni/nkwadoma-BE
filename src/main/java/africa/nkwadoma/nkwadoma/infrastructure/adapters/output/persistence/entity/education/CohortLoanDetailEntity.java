package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cohort_loanDetail", uniqueConstraints = {@UniqueConstraint(columnNames = {"cohort", "loanDetail_id"},
        name = "uk_cohort_loanDetail")})
public class CohortLoanDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @OneToOne
    private LoanDetailEntity loanDetail;
    private String cohort;
}
