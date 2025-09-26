package africa.nkwadoma.nkwadoma.application.ports.input.meedlportfolio;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Demography;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;

public interface PortfolioUseCase {
    Portfolio viewPortfolio() throws MeedlException;

    void setUpMeedlObligorLoanLimit(Portfolio portfolio) throws MeedlException;

    Portfolio viewMeedlObligorLoanLimit() throws MeedlException;

    Demography viewLoaneeDemography() throws MeedlException;
}
