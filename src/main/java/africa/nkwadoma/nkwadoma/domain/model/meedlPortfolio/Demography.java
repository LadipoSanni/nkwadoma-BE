package africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class Demography {

    private String id;
    private String name;

    private int maleCount;
    private int femaleCount;
    private int totalGenderCount;

    private int age17To25Count;
    private int age25To35Count;
    private int age35To45Count;

    private int southEastCount;
    private int southWestCount;
    private int southSouthCount;
    private int northEastCount;
    private int northWestCount;
    private int northCentralCount;

    private int oLevelCount;
    private int tertiaryCount;


    private double malePercentage;
    private double femalePercentage;
    private double age17To25Percentage;
    private double age26To35Percentage;
    private double age35To45Percentage;
    private double southEastPercentage;
    private double southWestPercentage;
    private double southSouthPercentage;
    private double northEastPercentage;
    private double northWestPercentage;
    private double northCenterPercentage;
    private double olevelPercentage;
    private double tertiaryPercentage;
}
