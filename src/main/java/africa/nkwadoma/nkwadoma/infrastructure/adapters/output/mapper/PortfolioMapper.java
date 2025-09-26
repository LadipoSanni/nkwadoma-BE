package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper;

import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlportfolio.PortfolioEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanMetricsProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PortfolioMapper {
    PortfolioEntity toPortfolioEntity(Portfolio portfolio);

    Portfolio toMeedlPortfolio(PortfolioEntity portfolioEntity);

    @Mapping(target = "totalNumberOfLoans", source = "totalNumberOfLoans")
    @Mapping(target = "loanReferralPercentage", source = "loanReferralPercentage")
    @Mapping(target = "loanRequestPercentage", source = "loanRequestPercentage")
    @Mapping(target = "loanDisbursalPercentage", source = "loanDisbursalPercentage")
    @Mapping(target = "loanOfferPercentage", source = "loanOfferPercentage")
    void updateLoanMetricsOnPortfolio(@MappingTarget Portfolio portfolio, LoanMetricsProjection loanMetricsProjection);
}
