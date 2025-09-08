package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanBook;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@ToString
public class RepaymentScheduleResponse {


    private BigDecimal sumTotal;
    private int tenor;
    private int moratorium;
    private List<RepaymentScheduleEntry> repaymentScheduleEntries;
}
