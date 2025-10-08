package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanProduct;

import africa.nkwadoma.nkwadoma.domain.enums.Product;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VendorResponse {
    private String id;
    private Product product;
    private String vendorName;
    private String createdAt;
    private String termsAndConditions;
    private BigDecimal costOfService;
    private int duration;
}
