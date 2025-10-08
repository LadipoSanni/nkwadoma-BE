package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Slf4j
@Entity
public class VendorEntity {
    @Id
    @UuidGenerator
    private String id;
    private LocalDateTime createdAt;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "vendor_provider_services",
            joinColumns = @JoinColumn(name = "vendor_id")
    )
    @Column(name = "provider_service")
    private Set<String> providerServices;

    private String vendorName;
    private String termsAndConditions;
    private BigDecimal costOfService;
    private int duration;
}
