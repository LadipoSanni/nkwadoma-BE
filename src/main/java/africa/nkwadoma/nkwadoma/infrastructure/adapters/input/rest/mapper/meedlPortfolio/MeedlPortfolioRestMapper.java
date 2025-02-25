package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.meedlPortfolio;

import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.MeedlPortfolio;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.meedlPortfolio.MeedlPortfolioResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MeedlPortfolioRestMapper {

    MeedlPortfolioResponse toMeedlPortfolioResponse(MeedlPortfolio meedlPortfolio);
}
