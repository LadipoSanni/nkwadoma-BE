package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoaneeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String alternatePhoneNumber;
    private String alternateContactAddress;
    private String alternateEmail;
    private String cohortId;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
