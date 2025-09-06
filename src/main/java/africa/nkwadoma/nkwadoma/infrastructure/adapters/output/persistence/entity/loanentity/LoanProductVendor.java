package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class LoanProductVendor {
    @Id
    @UuidGenerator
    private String id;
    @ManyToOne
    private VendorEntity vendorEntity;
    @ManyToOne
    private LoanProductEntity loanProductEntity;
}
