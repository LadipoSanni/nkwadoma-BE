package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AllCohortsRequest {
    @NotBlank(message = "Program ID is required")
    private String programId;
    private int pageNumber;
    private int pageSize;

    public int getPageSize() {
        int defaultPageSize = BigInteger.TEN.intValue();
        return this.pageSize == 0 ? defaultPageSize : this.pageSize;
    }
}
