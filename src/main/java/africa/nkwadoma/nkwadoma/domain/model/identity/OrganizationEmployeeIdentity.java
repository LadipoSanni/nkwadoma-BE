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
    private UserIdentity middlUser;
    private String organization;
}
