package africa.nkwadoma.nkwadoma.application.ports.input.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;

public interface CreateNextOfKinUseCase {
    NextOfKin saveAdditionalDetails(NextOfKin nextOfKin) throws MeedlException;
}
