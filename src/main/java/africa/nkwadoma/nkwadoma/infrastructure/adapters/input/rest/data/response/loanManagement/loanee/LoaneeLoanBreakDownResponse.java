package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanee;

import lombok.*;

import java.math.BigDecimal;


@Builder
@Getter
@Setter
@ToString
public class LoaneeLoanBreakDownResponse {
    private String loaneeLoanBreakdownId;
    private String itemName;
    private BigDecimal itemAmount ;
    private String currency;
}
