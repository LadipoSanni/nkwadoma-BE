package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {
    private String refreshToken;
}
