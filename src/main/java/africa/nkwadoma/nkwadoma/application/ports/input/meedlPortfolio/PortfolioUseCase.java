package africa.nkwadoma.nkwadoma.application.ports.input.meedlPortfolio;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;

public interface PortfolioUseCase {
    Portfolio viewPortfolio() throws MeedlException;
}
