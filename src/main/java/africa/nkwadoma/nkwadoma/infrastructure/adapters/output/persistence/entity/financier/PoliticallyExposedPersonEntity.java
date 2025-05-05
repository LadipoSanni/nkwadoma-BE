package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.financier;

import africa.nkwadoma.nkwadoma.domain.enums.identity.Country;
import africa.nkwadoma.nkwadoma.domain.enums.identity.UserRelationship;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity
public class PoliticallyExposedPersonEntity {
    @Id
    @UuidGenerator
    private String id;
    private String positionHeld;
    @Enumerated(EnumType.STRING)
    private Country country;
    @Enumerated(EnumType.STRING)
    private UserRelationship relationship;
    private String AdditionalInformation;
}
