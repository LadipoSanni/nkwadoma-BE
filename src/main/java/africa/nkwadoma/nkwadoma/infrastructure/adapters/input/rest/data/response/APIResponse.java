package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class APIResponse<T>{
    private String message;
    private T data;
    private String statusCode;
}
