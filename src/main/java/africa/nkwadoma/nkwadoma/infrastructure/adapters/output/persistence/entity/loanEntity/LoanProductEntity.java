package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "loan_product")
public class LoanProductEntity {
    @Id
    @UuidGenerator
    private String id;
}
