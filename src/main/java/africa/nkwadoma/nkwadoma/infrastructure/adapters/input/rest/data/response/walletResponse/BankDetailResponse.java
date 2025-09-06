package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.walletResponse;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class BankDetailResponse {
    private String id;
    private String bankName;
    private String bankNumber;
}
