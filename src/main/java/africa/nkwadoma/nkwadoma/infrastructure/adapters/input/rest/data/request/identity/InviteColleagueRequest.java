package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity;

import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InviteColleagueRequest {

    private String firstName;
    private String lastName;
    private IdentityRole role;
    private String email;

}
