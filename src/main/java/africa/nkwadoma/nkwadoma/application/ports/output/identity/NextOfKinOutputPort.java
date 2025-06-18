package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;

import java.util.*;

public interface NextOfKinOutputPort {
    NextOfKin save(NextOfKin nextOfKin) throws MeedlException;

    void deleteNextOfKin(String nextOfKinId) throws MeedlException;

    NextOfKin findByEmail(String email) throws MeedlException;
    
//    Optional<NextOfKin> findByUserId(String id) throws MeedlException;
}
