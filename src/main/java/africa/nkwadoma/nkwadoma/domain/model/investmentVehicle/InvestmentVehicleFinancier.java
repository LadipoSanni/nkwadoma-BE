package africa.nkwadoma.nkwadoma.domain.model.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;

import java.util.*;

@Slf4j
@Getter
@Setter
@Builder
public class InvestmentVehicleFinancier {

    private List<OrganizationIdentity> organizations;
    private List<UserIdentity> individuals;
    private String createdBy;
    private String investmentVehicleId;

    public void validateIndividuals() throws MeedlException {
        for (UserIdentity userIdentity : individuals) {
            validateIndividualEmail(userIdentity);
            MeedlValidator.validateDataElement(userIdentity.getFirstName(), "First Name cannot be empty");
            MeedlValidator.validateDataElement(userIdentity.getLastName(), "Last Name cannot be empty");
        }
    }


    private static void validateIndividualEmail(UserIdentity userIdentity) throws MeedlException {
        try {
            MeedlValidator.validateObjectInstance(userIdentity, "User details cannot be empty");
            MeedlValidator.validateEmail(userIdentity.getEmail());
        } catch (MeedlException e) {
            throw new MeedlException(e.getMessage() + " for : "+ userIdentity.getEmail());
        }
    }
}
