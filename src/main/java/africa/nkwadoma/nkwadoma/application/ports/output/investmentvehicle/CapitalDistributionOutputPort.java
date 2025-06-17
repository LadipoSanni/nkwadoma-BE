package africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.CapitalDistribution;

public interface CapitalDistributionOutputPort {
    CapitalDistribution save(CapitalDistribution capitalDistribution) throws MeedlException;

    void deleteById(String capitalId) throws MeedlException;
}
