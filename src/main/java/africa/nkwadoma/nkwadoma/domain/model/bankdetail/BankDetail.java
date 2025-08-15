package africa.nkwadoma.nkwadoma.domain.model.bankdetail;

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
    private String bankName;
    private String bankNumber;
    private String response;

    public void validate() throws MeedlException {
        MeedlValidator.validateDataElement(this.bankName, "Bank account name is required.");
        MeedlValidator.validateAccountNumber(this.bankNumber, "Bank account number is required.");
    }
}
