package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlPortfolio;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Setter
@Getter
@Entity
public class MeedlPortfolioEntity {

    @Id
    @UuidGenerator
    private String id;
    private String portfolioName;
    private int totalNumberOfInvestmentVehicles;
    private int totalNumberOfCommercialFundsInvestmentVehicles;
    private int totalNumberOfEndowmentFundsInvestmentVehicles;
    private int totalNumberOfFinanciers;
    private int totalNumberOfIndividualFinanciers;
    private int totalNumberOfInstitutionalFinanciers;

}
