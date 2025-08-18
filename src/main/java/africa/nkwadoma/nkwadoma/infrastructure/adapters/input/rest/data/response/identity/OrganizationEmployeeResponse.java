package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class OrganizationEmployeeResponse {
    private String id;
    private String fullName;
    private String firstName;
    private String lastName;
    private String email;
    private String userId;
    private String requestedBy;
    private String createdBy;
    private LocalDateTime createdAt;
    private ActivationStatus activationStatus;
    private IdentityRole role;
}
