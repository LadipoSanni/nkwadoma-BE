package africa.nkwadoma.nkwadoma.domain.model.identity;

import africa.nkwadoma.nkwadoma.domain.model.education.*;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrganizationServiceOffering {
    private String id;
    private ServiceOffering serviceOffering;
    private String organizationId;
}
