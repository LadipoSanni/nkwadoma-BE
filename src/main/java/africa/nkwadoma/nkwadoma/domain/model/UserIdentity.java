package africa.nkwadoma.nkwadoma.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserIdentity {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean isVerified;
    private boolean isDisabled;
    private String createdAt;
}
