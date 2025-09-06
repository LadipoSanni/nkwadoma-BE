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
public class ApiResponse<T>{
    private String message;
    private T data;
    private String statusCode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timeStamp;
    private Map<String, BigDecimal> metadata;


    public static ApiResponse<Object> buildApiResponse(Object data, String message, String statusCode) {
        return  ApiResponse.builder()
                .data(data)
                .message(message)
                .statusCode(statusCode)
                .timeStamp(LocalDateTime.now())
                .build();
    }
}
