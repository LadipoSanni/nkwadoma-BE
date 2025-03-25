package africa.nkwadoma.nkwadoma.domain.model.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.AccreditationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
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

import java.math.BigDecimal;
import java.util.Set;

@Slf4j
@Getter
@Setter
@ToString
@Builder
public class Financier {
    private String id;
    private Cooperation cooperation;
    private UserIdentity individual;
    private String invitedBy;
    private FinancierType financierType;
    private ActivationStatus activationStatus;
    private AccreditationStatus accreditationStatus;
    private String investmentVehicleId;
    private BigDecimal amountToInvest;
    private Set<InvestmentVehicleDesignation> investmentVehicleDesignation;
    private int pageNumber;
    private int pageSize;

    private void validateUserIdentity() throws MeedlException {
        MeedlValidator.validateObjectInstance(individual, UserMessages.USER_IDENTITY_MUST_NOT_BE_EMPTY.getMessage());
        validateFinancierEmail(individual);
        MeedlValidator.validateDataElement(individual.getFirstName(), UserMessages.INVALID_FIRST_NAME.getMessage());
        MeedlValidator.validateDataElement(individual.getLastName(), UserMessages.INVALID_LAST_NAME.getMessage());
        MeedlValidator.validateEmail(individual.getEmail());
    }


    private static void validateFinancierEmail(UserIdentity userIdentity) throws MeedlException {
        try {
            MeedlValidator.validateObjectInstance(userIdentity, "User details cannot be empty");
            MeedlValidator.validateEmail(userIdentity.getEmail());
        } catch (MeedlException e) {
            log.error("validate financier email",e);
            throw new MeedlException(e.getMessage() + " for : "+ userIdentity.getEmail());
        }
    }

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(this.financierType, FinancierMessages.INVALID_FINANCIER_TYPE.getMessage());
        MeedlValidator.validateUUID(invitedBy, "Valid user identification for user performing this action is required");
        if (this.financierType == FinancierType.INDIVIDUAL){
            validateUserIdentity();
        }else{
            validateCooperation();
        }
    }

    private void validateCooperation() throws MeedlException {
        MeedlValidator.validateObjectInstance(cooperation, UserMessages.COOPERATION_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateObjectName(this.cooperation.getName(), " name cannot be empty", "Cooperation");
    }

    public void validateKyc() throws MeedlException {
        MeedlValidator.validateUUID(id, FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        MeedlValidator.validateObjectInstance(individual.getBankDetail(), "Provide a valid bank detail.");
        MeedlValidator.validateUUID(individual.getBankDetail().getId(), "Provide a valid bank detail id.");
        individual.getBankDetail().validate();
        MeedlValidator.validateObjectInstance(individual.getNextOfKin(), "Provide a valid next of kin detail.");
        MeedlValidator.validateObjectInstance(individual.getNextOfKin().getId(), "Provide a valid next of kin detail id.");
        individual.getNextOfKin().validate();
        MeedlValidator.validateDataElement(individual.getNin(), "Nin is required");
        MeedlValidator.validateDataElement(individual.getTaxId(), "Tax id is required");
        MeedlValidator.validateDataElement(individual.getAddress(), "Address is required");
    }
}
