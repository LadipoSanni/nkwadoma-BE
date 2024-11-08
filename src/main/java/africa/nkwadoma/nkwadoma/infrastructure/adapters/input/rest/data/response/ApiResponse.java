package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class ApiResponse<T>{
    private String message;
    private T data;
    private String statusCode;
}
