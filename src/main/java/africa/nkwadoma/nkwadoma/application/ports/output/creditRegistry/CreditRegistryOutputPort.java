package africa.nkwadoma.nkwadoma.application.ports.output.creditRegistry;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.CreditRegistryFindDetailResponse;

public interface CreditRegistryOutputPort {
    int getCreditScoreWithBvn(String bvn) throws MeedlException;
    int getCreditScoreWithRegistryId(String registryId, String sessionCode) throws MeedlException;
    String getSessionCode();
    CreditRegistryFindDetailResponse getCustomerDetails(String customerQuery, String sessionCode) throws MeedlException;
}
