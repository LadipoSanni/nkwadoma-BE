package africa.nkwadoma.nkwadoma.domain.model.identity;

import lombok.*;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationEmployeeIdentity {
    private String id;
//    private OrganizationIdentity organization;
//    private UserIdentity middlUser;
    private UserIdentity middlUser;
    private String organization;
}
