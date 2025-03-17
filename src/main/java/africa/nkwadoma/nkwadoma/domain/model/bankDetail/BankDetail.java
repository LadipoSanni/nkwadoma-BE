package africa.nkwadoma.nkwadoma.domain.model.bankDetail;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankDetail {
    private String id;
    private String accountName;
    private String accountNumber;

    public void validate() throws MeedlException {
        MeedlValidator.validateDataElement(accountName, "Bank account name is required.");
        MeedlValidator.validateAccountNumber(accountNumber, "Bank account number is required.");
    }
}
