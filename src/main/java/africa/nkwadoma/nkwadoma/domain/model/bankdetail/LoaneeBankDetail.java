package africa.nkwadoma.nkwadoma.domain.model.bankdetail;

import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoaneeBankDetail {

    private String id;
    private Loanee loanee;
    private BankDetail bankDetail;
}
