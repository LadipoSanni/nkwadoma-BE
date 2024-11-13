package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AllLoaneeInCohortRequest {

    private String cohortId;
    private int pageSize;
    private int pageNumber;


    public int getPageSize() {
        int defaultPageSize = BigInteger.TEN.intValue();
        return this.pageSize == 0 ? defaultPageSize : this.pageSize;
    }
}
