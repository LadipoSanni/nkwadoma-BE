package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ProgramsRequest {
    private int pageSize;
    private int pageNumber;
}
