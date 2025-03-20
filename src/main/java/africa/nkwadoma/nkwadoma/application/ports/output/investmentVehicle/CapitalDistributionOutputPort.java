package africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.CapitalDistribution;

public interface CapitalDistributionOutputPort {
    CapitalDistribution save(CapitalDistribution capitalDistribution) throws MeedlException;

    void deleteById(String capitalId) throws MeedlException;
}
