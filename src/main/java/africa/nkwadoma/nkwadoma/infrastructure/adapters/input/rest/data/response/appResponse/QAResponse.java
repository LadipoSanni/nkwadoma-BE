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
public class QAResponse <T>  {
    private String id;
    private String email;
    private String message;
    private T data;
    private String statusCode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timeStamp;
    private Map<String, BigDecimal> metadata;

    public static QAResponse build(String id){
        QAResponse qaResponse = new QAResponse();
        qaResponse.setId(id);
        return qaResponse;
    }
    public static QAResponse build(String email, String id){
        QAResponse qaResponse = new QAResponse();
        qaResponse.setId(id);
        qaResponse.setEmail(email);
        return qaResponse;
    }
    public static QAResponse build(){
        return new QAResponse();
    }
}
