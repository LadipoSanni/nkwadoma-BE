package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.MeedlPortfolio;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlPortfolio.MeedlPortfolioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MeedlPortfolioMapper {
    MeedlPortfolioEntity toMeedlPortfolioEntity(MeedlPortfolio meedlPortfolio);

    MeedlPortfolio toMeedlPortfolio(MeedlPortfolioEntity meedlPortfolioEntity);
}
