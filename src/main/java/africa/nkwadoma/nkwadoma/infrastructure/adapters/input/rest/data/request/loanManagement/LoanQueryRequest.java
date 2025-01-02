package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanQueryRequest {
    @NotBlank(message = "Organization ID is required")
    private String organizationId;
    private int pageSize;
    private int pageNumber;

    public int getPageSize() {
        int defaultValue = BigInteger.TEN.intValue();
        return this.pageSize == BigInteger.ZERO.intValue() ? defaultValue : this.pageSize;
    }
}
