package africa.nkwadoma.nkwadoma.domain.model.identity;

import africa.nkwadoma.nkwadoma.domain.model.education.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationIdentity {
    private String id;
    private String name;
    private String email;
    private String websiteAddress;
    private String invitedDate;
    private String rcNumber;
    private String tin;
    private String phoneNumber;
    private int numberOfPrograms;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime timeUpdated;
    private List<ServiceOffering> serviceOfferings;
    private List<OrganizationEmployeeIdentity> organizationEmployees;

    private String logoImage;
    private String bannerImage;
    private String address;
    private String officeAddress;
}
