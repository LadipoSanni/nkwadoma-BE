package africa.nkwadoma.nkwadoma.domain.model.bankdetail;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankDetail {
    private String id;
    private String userId;
    private String bankName;
    private String bankNumber;
    private String response;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;
    private String updatedBy;
    private ActivationStatus activationStatus;
    private UserIdentity userIdentity;

    public void validate() throws MeedlException {
        MeedlValidator.validateDataElement(this.bankName, "Bank account name is required.");
        MeedlValidator.validateAccountNumber(this.bankNumber, "Bank account number is required.");
    }
}
