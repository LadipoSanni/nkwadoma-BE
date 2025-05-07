package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@ToString
@Entity
public class FinancierPoliticallyExposedPersonEntity {
    @Id
    @UuidGenerator
    private String id;
    @OneToOne
    private FinancierEntity financier;
    @OneToOne
    private PoliticallyExposedPersonEntity politicallyExposedPerson;
}
