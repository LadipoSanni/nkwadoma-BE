package africa.nkwadoma.nkwadoma.application.ports.output.meedlPortfolio;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;

public interface PortfolioOutputPort {


    Portfolio save(Portfolio portfolio) throws MeedlException;

    Portfolio findPortfolio(Portfolio portfolio) throws MeedlException;

    void delete(String id);
}
