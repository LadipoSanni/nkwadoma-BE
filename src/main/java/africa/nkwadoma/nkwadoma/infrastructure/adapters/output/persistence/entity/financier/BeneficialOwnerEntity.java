package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity
public class BeneficialOwnerEntity {
    @Id
    @UuidGenerator
    private String id;
}
