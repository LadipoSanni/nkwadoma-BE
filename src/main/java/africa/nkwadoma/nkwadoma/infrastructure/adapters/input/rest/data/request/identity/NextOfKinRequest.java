package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class NextOfKinRequest {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String nextOfKinRelationship;
    private String contactAddress;
    private String alternateEmail;
    private String alternatePhoneNumber;
    private String alternateContactAddress;
}
