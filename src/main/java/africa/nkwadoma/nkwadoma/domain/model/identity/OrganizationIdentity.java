package africa.nkwadoma.nkwadoma.domain.model.identity;

import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationIdentity {
    private String id;
    private String industry;
    private String name;
    private String email;
    private String websiteAddress;
    private String invitedDate;
    private String rcNumber;
    private String tin;
    private String phoneNumber;
//    private List<UserIdentity> organizationEmployees;
    private List<OrganizationEmployeeIdentity> organizationEmployees;

}
