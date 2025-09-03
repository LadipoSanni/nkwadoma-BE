package africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class Demography {

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
    private int totalGeographicCount;

    private int oLevelCount;
    private int tertiaryCount;
    private int totalEducationLevelCount;
}
