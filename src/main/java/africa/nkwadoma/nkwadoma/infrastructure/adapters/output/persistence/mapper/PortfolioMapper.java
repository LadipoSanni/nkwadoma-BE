package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlPortfolio.PortfolioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PortfolioMapper {
    PortfolioEntity toPortfolioEntity(Portfolio portfolio);

    Portfolio toMeedlPortfolio(PortfolioEntity portfolioEntity);
}
