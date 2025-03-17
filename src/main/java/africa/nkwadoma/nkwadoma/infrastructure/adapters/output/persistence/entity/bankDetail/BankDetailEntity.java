package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.bankDetail;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class BankDetailEntity {
    @Id
    private String id;
    private String bankAccountName;
    private String bankAccountNumber;
}
