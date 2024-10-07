package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InviteOrganizationResponse {

    private String firstName;
    private String lastName;
    private String createdBy;
    private String role;
    private String email;
}
