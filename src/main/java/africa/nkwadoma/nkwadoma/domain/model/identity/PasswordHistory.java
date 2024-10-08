package africa.nkwadoma.nkwadoma.domain.model.identity;

import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordHistory {
    private String id;
    private String middlUser;
    private String password;
}
