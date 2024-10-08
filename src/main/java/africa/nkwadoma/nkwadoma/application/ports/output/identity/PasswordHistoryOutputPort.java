package africa.nkwadoma.nkwadoma.application.ports.output.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.PasswordHistory;

import java.util.List;

public interface PasswordHistoryOutputPort {
    PasswordHistory save(PasswordHistory passwordHistory);
    List<PasswordHistory> findByUser(String id)throws MiddlException;
}
