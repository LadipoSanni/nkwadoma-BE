package africa.nkwadoma.nkwadoma.domain.model.bankdetail;

import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class OrganizationBankDetail {
    private String id;
    private OrganizationIdentity organizationIdentity;
    private BankDetail bankDetail;
}
