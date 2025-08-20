package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class CooperateFinancierResponse {

    private String cooperateFinancierId;
    private String firstName;
    private String lastName;
    private String email;
    private String inviteeName;
    private IdentityRole role;
    private LocalDateTime createdAt;
    private ActivationStatus activationStatus;
}
