package africa.nkwadoma.nkwadoma.domain.model.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.AccreditationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleDesignation;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankDetail.BankDetail;
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
    private FinancierType financierType;
    private UserIdentity individual;
    private String invitedBy;
    private ActivationStatus activationStatus;
    private AccreditationStatus accreditationStatus;
    private String investmentVehicleId;
    private String address;
    private String nin;
    private String taxId;
    private BankDetail bankDetail;
    private Set<InvestmentVehicleDesignation> investmentVehicleDesignation;
    private int pageNumber;
    private int pageSize;

    private void validateUserIdentity() throws MeedlException {
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

    public void validateKyc() throws MeedlException {
        MeedlValidator.validateObjectInstance(bankDetail, "Provide a valid bank detail.");
        MeedlValidator.validateDataElement(bankDetail.getAccountName(), "Bank account name is required.");
        MeedlValidator.validateDataElement(bankDetail.getAccountNumber(), "Bank account number is required.");
        MeedlValidator.validateObjectInstance(individual.getNextOfKin(), "Provide a valid next of kin detail.");
        MeedlValidator.validateDataElement(nin, "Nin is required");
        MeedlValidator.validateDataElement(taxId, "Tax id is required");
        MeedlValidator.validateDataElement(address, "Address is required");
        MeedlValidator.validateAccountNumber(bankDetail.getAccountNumber(), "Account number is required");
    }
}
