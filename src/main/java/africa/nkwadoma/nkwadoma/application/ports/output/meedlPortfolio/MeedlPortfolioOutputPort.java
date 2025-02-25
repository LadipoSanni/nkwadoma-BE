package africa.nkwadoma.nkwadoma.application.ports.output.meedlPortfolio;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.MeedlPortfolio;

public interface MeedlPortfolioOutputPort {


    MeedlPortfolio save(MeedlPortfolio meedlPortfolio) throws MeedlException;

    MeedlPortfolio findMeedlPortfolio();

    void delete(String id);
}
