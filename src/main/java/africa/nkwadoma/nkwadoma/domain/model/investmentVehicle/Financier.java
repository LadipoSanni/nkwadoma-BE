package africa.nkwadoma.nkwadoma.domain.model.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.AccreditationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleDesignation;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

@Slf4j
@Getter
@Setter
@ToString
@Builder
public class Financier {
    private String id;
    private String organizationName;
    private UserIdentity individual;
    private String invitedBy;
    private ActivationStatus activationStatus;
    private AccreditationStatus accreditationStatus;
    private String investmentVehicleId;
    private Set<InvestmentVehicleDesignation> investmentVehicleDesignation;
    private int pageNumber;
    private int pageSize;

    public void validateUserIdentity() throws MeedlException {
            validateIndividualEmail(individual);
            MeedlValidator.validateDataElement(individual.getFirstName(), UserMessages.INVALID_FIRST_NAME.getMessage());
            MeedlValidator.validateDataElement(individual.getLastName(), UserMessages.INVALID_LAST_NAME.getMessage());
            MeedlValidator.validateEmail(individual.getEmail());
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
        validateUserIdentity();
        MeedlValidator.validateUUID(invitedBy, "Valid user id performing this action is required");
    }
}
