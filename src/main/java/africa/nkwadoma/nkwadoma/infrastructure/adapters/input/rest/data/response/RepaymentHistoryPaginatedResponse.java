package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class RepaymentHistoryPaginatedResponse<T>{

    private List<T> body;
    private boolean hasNextPage;
    private long totalPages;
    public int pageNumber;
    public int pageSize;
    private Integer firstYear;
    private Integer lastYear;

}
