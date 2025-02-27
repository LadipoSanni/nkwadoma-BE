package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.meedlPortfolio;

import africa.nkwadoma.nkwadoma.application.ports.output.meedlPortfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlPortfolio.PortfolioEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.PortfolioMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.meedlPortfolio.PortfolioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PortfolioAdapter implements PortfolioOutputPort {


    private final PortfolioMapper portfolioMapper;
    private final PortfolioEntityRepository portfolioEntityRepository;

    @Override
    public Portfolio save(Portfolio portfolio) throws MeedlException {
        MeedlValidator.validateObjectInstance(portfolio);
        PortfolioEntity portfolioEntity
                = portfolioMapper.toPortfolioEntity(portfolio);
        portfolioEntity = portfolioEntityRepository.save(portfolioEntity);
        return portfolioMapper.toMeedlPortfolio(portfolioEntity);
    }

    @Override
    public Portfolio findPortfolio(Portfolio portfolio) throws MeedlException {
        MeedlValidator.validateObjectName(portfolio.getPortfolioName(),"Portfolio name cannot be empty");
            PortfolioEntity portfolioEntity =
                    portfolioEntityRepository.findByPortfolioName(portfolio.getPortfolioName());
        return portfolioMapper.toMeedlPortfolio(portfolioEntity);
    }

    @Override
    public void delete(String id) {
        portfolioEntityRepository.deleteById(id);
    }

}
