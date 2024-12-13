package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;


import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoaneeLoanBreakdownRequest {

    private String loanBreakdownId;
    private String itemName;
    private BigDecimal itemAmount;
    private String currency;

}
