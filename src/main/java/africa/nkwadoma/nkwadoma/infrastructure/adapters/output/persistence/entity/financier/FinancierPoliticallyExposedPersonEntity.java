package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier;

import jakarta.persistence.*;
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
    @ManyToOne
    private FinancierEntity financier;
    @OneToOne
    private PoliticallyExposedPersonEntity politicallyExposedPerson;
}
