package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement;

import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LoanBreakdownResponse {
    private String loanBreakdownId;
    private String itemName;
    private BigDecimal itemAmount = BigDecimal.ZERO;
    private String currency;

}
