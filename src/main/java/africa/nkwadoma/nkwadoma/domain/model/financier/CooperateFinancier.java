package africa.nkwadoma.nkwadoma.domain.model.financier;


import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.Cooperation;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@ToString
public class CooperateFinancier {

    private String id;
    private Cooperation cooperate;
    private Financier financier;
    private ActivationStatus activationStatus;
    private String firstName;
    private String lastName;
    private String email;
    private String inviteeName;
    private IdentityRole role;
    private LocalDateTime createdAt;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(cooperate,"Cooperate cannot be empty");
        MeedlValidator.validateObjectInstance(financier,"Financier cannot be empty");
        MeedlValidator.validateObjectInstance(activationStatus,"Activation status cannot be empty");
    }
}
