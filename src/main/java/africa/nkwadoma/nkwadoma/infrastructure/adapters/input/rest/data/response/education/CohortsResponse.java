package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class CohortsResponse {

    private String id;
    private String name;
    private int numberOfLoanees = 0;
    private LocalDate startDate;
    private BigDecimal tuitionAmount = BigDecimal.ZERO;
    private BigDecimal amountRequested = BigDecimal.ZERO;
    private BigDecimal amountReceived = BigDecimal.ZERO;
    private BigDecimal amountOutstanding = BigDecimal.ZERO;
}
