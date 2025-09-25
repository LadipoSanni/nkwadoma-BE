package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import lombok.*;
import lombok.extern.slf4j.*;

import java.math.BigDecimal;

@Getter
@Setter
@Slf4j
public class Vendor {
    private String id;
    private Product product;
    private String vendorName;
    private String termsAndConditions;
    private BigDecimal costOfService;
    private int duration;
}