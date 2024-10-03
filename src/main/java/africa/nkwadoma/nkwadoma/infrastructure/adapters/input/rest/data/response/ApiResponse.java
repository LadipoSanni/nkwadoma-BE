package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private String message;
    private boolean isSuccessful;
    private HttpStatus httpStatus;
}
