package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlportfolio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;


@Setter
@Getter
@Entity
@ToString
public class DemographyEntity {

    @Id
    @UuidGenerator
    private String id;
    private String name;
    private int maleCount;
    private int femaleCount;
    private int totalGenderCount;
    @Column(name = "age_17_to_25_count")
    private int age17To25Count;
    @Column(name = "age_25_to_35_count")
    private int age25To35Count;
    @Column(name = "age_35_to_45_count")
    private int age35To45Count;
    @Column(name = "south_east_count")
    private int southEastCount;
    @Column(name = "south_west_count")
    private int southWestCount;
    @Column(name = "south_south_count")
    private int southSouthCount;
    @Column(name = "north_east_count")
    private int northEastCount;
    @Column(name = "north_west_count")
    private int northWestCount;
    @Column(name = "north_central_count")
    private int northCentralCount;
    @Column(name = "non_nigerian")
    private int nonNigerian;
    @Column(name = "o_level_count")
    private int oLevelCount;
    @Column(name = "tertiary_count")
    private int tertiaryCount;

}
