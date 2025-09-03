package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class QAResponse {
    private String id;
    private String email;
    private String message;
    private String statusCode;

}
