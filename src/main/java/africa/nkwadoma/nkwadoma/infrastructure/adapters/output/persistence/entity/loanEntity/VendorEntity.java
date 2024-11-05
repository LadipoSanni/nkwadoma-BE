package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity;

import africa.nkwadoma.nkwadoma.domain.enums.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Slf4j
@Entity
public class VendorEntity {
    @Id
    @UuidGenerator
    private String id;
    private Product product;
    @Column(unique = true)
    private String vendorName;
    private String termsAndConditions;
}
