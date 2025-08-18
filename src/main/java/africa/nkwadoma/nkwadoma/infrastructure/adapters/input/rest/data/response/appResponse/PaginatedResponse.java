package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse;

import lombok.*;

import java.util.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class PaginatedResponse<T> {
    private List<T> body;
    private boolean hasNextPage;
    private long totalPages;
    private long totalElement;
    public int pageNumber;
    public int pageSize;
}
