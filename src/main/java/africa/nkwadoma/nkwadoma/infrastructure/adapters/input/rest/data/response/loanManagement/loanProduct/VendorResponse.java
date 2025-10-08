package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanProduct;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
public class VendorResponse {
    private String id;
    private Set<String> providerServices;
    private String vendorName;
    private String createdAt;
    private String termsAndConditions;
    private BigDecimal costOfService;
    private int duration;
}
