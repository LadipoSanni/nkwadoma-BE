package africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.Cooperation;

public interface CooperationOutputPort {
    Cooperation save(Cooperation cooperation) throws MeedlException;

    Cooperation findById(String cooperationId) throws MeedlException;

    void deleteById(String cooperationId) throws MeedlException;

    Cooperation findByName(String name) throws MeedlException;
}
