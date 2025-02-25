package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.meedlPortfolio;

import africa.nkwadoma.nkwadoma.application.ports.output.meedlPortfolio.MeedlPortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.MeedlPortfolio;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlPortfolio.MeedlPortfolioEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.MeedlPortfolioMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.meedlPortfolio.MeedlPortfolioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeedlPortfolioAdapter  implements MeedlPortfolioOutputPort {


    private final MeedlPortfolioMapper meedlPortfolioMapper;
    private final MeedlPortfolioEntityRepository meedlPortfolioEntityRepository;

    @Override
    public MeedlPortfolio save(MeedlPortfolio meedlPortfolio) throws MeedlException {
        MeedlValidator.validateObjectInstance(meedlPortfolio);
        MeedlPortfolioEntity meedlPortfolioEntity
                = meedlPortfolioMapper.toMeedlPortfolioEntity(meedlPortfolio);
        meedlPortfolioEntity = meedlPortfolioEntityRepository.save(meedlPortfolioEntity);
        return meedlPortfolioMapper.toMeedlPortfolio(meedlPortfolioEntity);
    }

    @Override
    public MeedlPortfolio findMeedlPortfolio() {
            MeedlPortfolioEntity meedlPortfolioEntity =
                    meedlPortfolioEntityRepository.findByPortfolioName("Meedl");
        return meedlPortfolioMapper.toMeedlPortfolio(meedlPortfolioEntity);
    }

    @Override
    public void delete(String id) {
        meedlPortfolioEntityRepository.deleteById(id);
    }

}
