package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlportfolio;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;


@Setter
@Getter
@Entity
public class DemographyEntity {

    @Id
    @UuidGenerator
    private String id;

    private String name;

    private int maleCount;
    private int femaleCount;
    private int totalGenderCount;

    private int age17To25Count;
    private int age25To35Count;
    private int age35To45Count;
    private int totalAgeCount;

    private int southEastCount;
    private int southWestCount;
    private int southSouthCount;
    private int northEastCount;
    private int northWestCount;
    private int northCentralCount;
    private int nonNigerian;
    private int totalGeographicCount;

    private int oLevelCount;
    private int tertiaryCount;
    private int totalEducationLevelCount;
}
