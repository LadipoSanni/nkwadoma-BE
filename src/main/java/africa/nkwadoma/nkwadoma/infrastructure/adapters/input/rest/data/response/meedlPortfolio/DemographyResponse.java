package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.meedlPortfolio;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class DemographyResponse {

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
