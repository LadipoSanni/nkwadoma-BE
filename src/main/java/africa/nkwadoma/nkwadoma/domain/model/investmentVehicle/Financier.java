package africa.nkwadoma.nkwadoma.domain.model.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Getter
@Setter
@Builder
public class Financier {
    private String id;
    private List<OrganizationIdentity> organizations;
    private List<UserIdentity> individuals;
    private String createdBy;
    private String investmentVehicleId;
    private InvestmentVehicleRole investmentVehicleRole;
    private int pageNumber;
    private int pageSize;

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

    public void validate() throws MeedlException {
        validateIndividuals();
        MeedlValidator.validateUUID(createdBy, "Valid user id performing this action is required");
        MeedlValidator.validateUUID(investmentVehicleId, InvestmentVehicleMessages.INVALID_INVESTMENT_VEHICLE_ID.getMessage());
    }
}
