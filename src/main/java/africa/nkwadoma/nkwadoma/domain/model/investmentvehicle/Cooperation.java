package africa.nkwadoma.nkwadoma.domain.model.investmentvehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@Getter
@Setter
@ToString
public class Cooperation {
    private String id;
    private String name;
    private String email;

    public void validate() throws MeedlException {
        log.info("Validating cooperation details...");
        MeedlValidator.validateObjectName(this.name,"name cannot be empty","Cooperation name");
        MeedlValidator.validateEmail(this.email);
    }
}
