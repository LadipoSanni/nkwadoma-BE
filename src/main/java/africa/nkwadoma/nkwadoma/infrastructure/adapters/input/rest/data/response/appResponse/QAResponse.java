package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class QAResponse {
    private String id;
    private String email;

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
