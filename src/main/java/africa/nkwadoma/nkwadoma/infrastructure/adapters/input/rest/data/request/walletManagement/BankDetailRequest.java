package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.walletManagement;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankDetailRequest {
    private String id;
    private String bankName;
    private String bankNumber;
    private ActivationStatus activationStatus;
}
