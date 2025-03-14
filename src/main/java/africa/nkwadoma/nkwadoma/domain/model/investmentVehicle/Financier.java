package africa.nkwadoma.nkwadoma.domain.model.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleDesignation;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
@Getter
@Setter
@ToString
@Builder
public class Financier {
    private String id;
    private String organizationName;
    private UserIdentity userIdentity;
    private String invitedBy;
    private FinancierType financierType;
    private ActivationStatus activationStatus;
    private String investmentVehicleId;
    private Set<InvestmentVehicleDesignation> investmentVehicleDesignation;
    private int pageNumber;
    private int pageSize;

    public void validateUserIdentity() throws MeedlException {
            validateIndividualEmail(userIdentity);
            MeedlValidator.validateDataElement(userIdentity.getFirstName(), UserMessages.INVALID_FIRST_NAME.getMessage());
            MeedlValidator.validateDataElement(userIdentity.getLastName(), UserMessages.INVALID_LAST_NAME.getMessage());
            MeedlValidator.validateEmail(userIdentity.getEmail());
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
        MeedlValidator.validateObjectInstance(this.financierType, "Please specify if financier is individual or cooperate.");
        MeedlValidator.validateUUID(invitedBy, "Valid user identification for user performing this action is required");
        if (this.financierType == FinancierType.INDIVIDUAL){
            validateUserIdentity();
        }else{
            validateCooperation();
        }
    }

    private void validateCooperation() {

    }
}
