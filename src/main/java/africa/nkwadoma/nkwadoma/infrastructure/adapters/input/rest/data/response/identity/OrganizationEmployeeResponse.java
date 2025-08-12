package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity;

import africa.nkwadoma.nkwadoma.domain.enums.*;
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
    private LocalDateTime createdAt;
    private ActivationStatus activationStatus;
    private IdentityRole role;
}
