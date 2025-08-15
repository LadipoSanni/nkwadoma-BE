package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.walletManagement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankDetailRequest {
    private String bankName;
    private String bankNumber;
}
