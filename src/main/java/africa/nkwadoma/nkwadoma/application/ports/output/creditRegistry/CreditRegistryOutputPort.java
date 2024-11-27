package africa.nkwadoma.nkwadoma.application.ports.output.creditRegistry;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.CreditRegistryFindDetailResponse;

import java.util.List;

public interface CreditRegistryOutputPort {
    int getCreditScoreWithBvn(String bvn) throws MeedlException;
    int getCreditScoreWithRegistryId(List<String> registryIds, String sessionCode) throws MeedlException;
    String getSessionCode();
    CreditRegistryFindDetailResponse getCustomerDetails(String customerQuery, String sessionCode) throws MeedlException;
}
