package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.meedlportfolio;

import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlportfolio.PortfolioEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.PortfolioMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.meedlportfolio.PortfolioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PortfolioAdapter implements PortfolioOutputPort {


    private final PortfolioMapper portfolioMapper;
    private final PortfolioEntityRepository portfolioEntityRepository;

    @Override
    public Portfolio save(Portfolio portfolio) throws MeedlException {
        MeedlValidator.validateObjectInstance(portfolio,"Portfolio cannot be empty");
        PortfolioEntity portfolioEntity
                = portfolioMapper.toPortfolioEntity(portfolio);
        portfolioEntity = portfolioEntityRepository.save(portfolioEntity);
        return portfolioMapper.toMeedlPortfolio(portfolioEntity);
    }

    @Override
    public Portfolio findPortfolio(Portfolio portfolio) throws MeedlException {
        MeedlValidator.validateObjectName(portfolio.getPortfolioName(),"Portfolio name cannot be empty","portfolio");
            PortfolioEntity portfolioEntity =
                    portfolioEntityRepository.findByPortfolioName(portfolio.getPortfolioName());
        return portfolioMapper.toMeedlPortfolio(portfolioEntity);
    }

    @Override
    public void delete(String id) {
        portfolioEntityRepository.deleteById(id);
    }

}
