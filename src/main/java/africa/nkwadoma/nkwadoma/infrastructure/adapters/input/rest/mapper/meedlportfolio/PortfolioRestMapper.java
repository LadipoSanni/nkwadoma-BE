package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.meedlportfolio;

import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Demography;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.meedlPortfolio.DemographyResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.meedlPortfolio.PortfolioResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PortfolioRestMapper {

    PortfolioResponse toMeedlPortfolioResponse(Portfolio portfolio);

    DemographyResponse toDemographyResponse(Demography demography);
}
