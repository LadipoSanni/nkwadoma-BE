package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity;

import africa.nkwadoma.nkwadoma.domain.enums.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Slf4j
@Entity
public class VendorEntity {
    @Id
    @UuidGenerator
    private String id;
    private Product product;
    private LocalDateTime createdAt;
    private String vendorName;
    private String termsAndConditions;
    private BigDecimal costOfService;
    private int duration;
}
