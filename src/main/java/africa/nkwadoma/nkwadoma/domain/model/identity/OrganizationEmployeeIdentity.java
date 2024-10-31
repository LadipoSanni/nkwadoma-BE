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
    private UserIdentity meedlUser;
    private String organization;
}
