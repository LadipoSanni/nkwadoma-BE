package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;

public interface NextOfKinIdentityOutputPort {
    NextOfKin save(NextOfKin nextOfKin) throws MeedlException;

    void deleteNextOfKin(String nextOfKinId) throws MeedlException;

    NextOfKin findByEmail(String email) throws MeedlException;
}
