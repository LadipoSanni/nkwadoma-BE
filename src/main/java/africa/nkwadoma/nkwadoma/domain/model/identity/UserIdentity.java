package africa.nkwadoma.nkwadoma.domain.model.identity;

import lombok.*;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserIdentity {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean emailVerified;
    private boolean enabled;
    private String createdAt;
    private String role;
    private String createdBy;

//    private OrganizationIdentity employer;
}
