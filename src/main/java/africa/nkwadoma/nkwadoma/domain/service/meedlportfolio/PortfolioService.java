package africa.nkwadoma.nkwadoma.domain.service.meedlportfolio;

import africa.nkwadoma.nkwadoma.application.ports.input.meedlportfolio.PortfolioUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanMetricsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.DemographyOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Demography;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.PortfolioMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanMetricsProjection;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlConstants.MEEDL;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.loan.FinancialConstants.PERCENTAGE_BASE_INT;

@Slf4j
@Service
@AllArgsConstructor
public class PortfolioService implements PortfolioUseCase {

    private final PortfolioOutputPort portfolioOutputPort;
    private final LoanMetricsOutputPort loanMetricsOutputPort;
    private final PortfolioMapper portfolioMapper;
    private final DemographyOutputPort demographyOutputPort;


    @Override
    public Portfolio viewPortfolio() throws MeedlException {
        Portfolio portfolio = Portfolio.builder().portfolioName(MEEDL).build();
        portfolio = portfolioOutputPort.findPortfolio(portfolio);
        LoanMetricsProjection loanMetricsProjection = loanMetricsOutputPort.calculateAllMetrics();
        portfolioMapper.updateLoanMetricsOnPortfolio(portfolio,loanMetricsProjection);
        int totalVehicles = portfolio.getTotalNumberOfInvestmentVehicle();

        log.info("total vehicles: {}", totalVehicles);

        if (totalVehicles == 0) {
            portfolio.setEndowmentVehiclePercentage(0.0);
            portfolio.setCommercialVehiclePercentage(0.0);
        }

        double endowmentPercentage = ((double) portfolio.getTotalNumberOfEndowmentFundsInvestmentVehicle() / totalVehicles) * PERCENTAGE_BASE_INT;
        double commercialPercentage = ((double) portfolio.getTotalNumberOfCommercialFundsInvestmentVehicle() / totalVehicles) * PERCENTAGE_BASE_INT;

        portfolio.setEndowmentVehiclePercentage(endowmentPercentage);
        portfolio.setCommercialVehiclePercentage(commercialPercentage);
        return portfolio;
    }

    @Override
    public Demography viewLoaneeDemography() throws MeedlException {
        Demography demography = demographyOutputPort.findDemographyByName(MEEDL);

        calculateGenderPercentage(demography);

        calculateAgePercentage(demography);

        calculateGeographicPercentage(demography);

        calculateEducationPercentage(demography);

        return demography;
    }

    private static void calculateEducationPercentage(Demography demography) {
        if (demography.getTotalGenderCount() > 0) {
            double olevelPercentage = (double) demography.getOLevelCount() * PERCENTAGE_BASE_INT / demography.getTotalGenderCount();
            double tertiaryPercentage = (double) demography.getTertiaryCount() * PERCENTAGE_BASE_INT / demography.getTotalGenderCount();

            demography.setOlevelPercentage(olevelPercentage);
            demography.setTertiaryPercentage(tertiaryPercentage);
        }
    }

    private static void calculateGeographicPercentage(Demography demography) {
        if (demography.getTotalGenderCount() > 0) {
            double southEastPercentage = (double) demography.getSouthEastCount() * PERCENTAGE_BASE_INT / demography.getTotalGenderCount();
            double southWestPercentage = (double) demography.getSouthWestCount() * PERCENTAGE_BASE_INT / demography.getTotalGenderCount();
            double southSouthPercentage = (double) demography.getSouthSouthCount() * PERCENTAGE_BASE_INT / demography.getTotalGenderCount();
            double northEastPercentage = (double) demography.getNorthEastCount() * PERCENTAGE_BASE_INT / demography.getTotalGenderCount();
            double northWestPercentage = (double) demography.getNorthWestCount() * PERCENTAGE_BASE_INT /demography.getTotalGenderCount();
            double northCentralPercentage = (double) demography.getNorthCentralCount() * PERCENTAGE_BASE_INT / demography.getTotalGenderCount();

            demography.setSouthEastPercentage(southEastPercentage);
            demography.setSouthWestPercentage(southWestPercentage);
            demography.setSouthSouthPercentage(southSouthPercentage);
            demography.setNorthEastPercentage(northEastPercentage);
            demography.setNorthWestPercentage(northWestPercentage);
            demography.setNorthCenterPercentage(northCentralPercentage);
        }
    }

    private static void calculateAgePercentage(Demography demography) {
        if (demography.getTotalGenderCount() > 0) {
            double age17To25Percentage = (double) demography.getAge17To25Count() * PERCENTAGE_BASE_INT / demography.getTotalGenderCount();
            double age26To35Percentage = (double) demography.getAge25To35Count() * PERCENTAGE_BASE_INT / demography.getTotalGenderCount();
            double age35To45Percentage = (double) demography.getAge35To45Count() * PERCENTAGE_BASE_INT / demography.getTotalGenderCount();

            demography.setAge17To25Percentage(age17To25Percentage);
            demography.setAge26To35Percentage(age26To35Percentage);
            demography.setAge35To45Percentage(age35To45Percentage);
        }
    }

    private static void calculateGenderPercentage(Demography demography) {
        if (demography.getTotalGenderCount() > 0) {
            double malePercentage = (double) demography.getMaleCount() * PERCENTAGE_BASE_INT / demography.getTotalGenderCount();
            double femalePercentage = (double) demography.getFemaleCount() * PERCENTAGE_BASE_INT / demography.getTotalGenderCount();
            demography.setMalePercentage(malePercentage);
            demography.setFemalePercentage(femalePercentage);
        }
    }
}
