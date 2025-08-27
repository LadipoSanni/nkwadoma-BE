package africa.nkwadoma.nkwadoma.domain.model.bankdetail;

import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FinancierBankDetail {
    private String id;
    private Financier financier;
    private BankDetail bankDetail;
}
