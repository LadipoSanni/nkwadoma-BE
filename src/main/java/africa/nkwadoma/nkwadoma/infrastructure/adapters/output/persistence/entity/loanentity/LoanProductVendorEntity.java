package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class LoanProductVendorEntity {
    @Id
    @UuidGenerator
    private String id;
    @ManyToOne
    private VendorEntity vendorEntity;
    @ManyToOne
    private LoanProductEntity loanProductEntity;
}
