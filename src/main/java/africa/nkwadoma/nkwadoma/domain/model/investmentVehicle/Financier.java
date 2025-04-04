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
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Slf4j
@Getter
@Setter
@ToString
@Builder
public class Financier {
    private String id;
    private Cooperation cooperation;
    private FinancierType financierType;
    private UserIdentity userIdentity;
    private ActivationStatus activationStatus;
    private AccreditationStatus accreditationStatus;
    private String investmentVehicleId;
    private BigDecimal amountToInvest;
    private Set<InvestmentVehicleDesignation> investmentVehicleDesignation;
    private int pageNumber;
    private int pageSize;
    private boolean declarationAndAgreement;
    private boolean politicallyExposed;
    private List<PoliticalPartiesExposedTo> politicalPartiesExposedTo;


    private void validateUserIdentity() throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, UserMessages.USER_IDENTITY_MUST_NOT_BE_EMPTY.getMessage());
        validateFinancierEmail(userIdentity);
        MeedlValidator.validateDataElement(userIdentity.getFirstName(), UserMessages.INVALID_FIRST_NAME.getMessage());
        MeedlValidator.validateDataElement(userIdentity.getLastName(), UserMessages.INVALID_LAST_NAME.getMessage());
        MeedlValidator.validateEmail(userIdentity.getEmail());
        MeedlValidator.validateObjectInstance(userIdentity, UserMessages.USER_IDENTITY_MUST_NOT_BE_EMPTY.getMessage());

        validateFinancierEmail(userIdentity);
        MeedlValidator.validateDataElement(userIdentity.getFirstName(), UserMessages.INVALID_FIRST_NAME.getMessage());
        MeedlValidator.validateDataElement(userIdentity.getLastName(), UserMessages.INVALID_LAST_NAME.getMessage());
        MeedlValidator.validateEmail(userIdentity.getEmail());
    }


    private static void validateFinancierEmail(UserIdentity userIdentity) throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, "User details cannot be empty");
        try {
            MeedlValidator.validateObjectInstance(userIdentity, "User details cannot be empty");
            MeedlValidator.validateEmail(userIdentity.getEmail());
        } catch (MeedlException e) {
            log.error("validate financier email",e);
            throw new MeedlException(e.getMessage() + " for : "+ userIdentity.getEmail());
        }
    }

    public void validate() throws MeedlException {
        if (MeedlValidator.isNotValidId(this.id)) {
            MeedlValidator.validateObjectInstance(this.financierType, FinancierMessages.INVALID_FINANCIER_TYPE.getMessage());
            MeedlValidator.validateObjectInstance(this.getUserIdentity(), UserMessages.USER_IDENTITY_MUST_NOT_BE_EMPTY.getMessage());
            MeedlValidator.validateUUID(this.getUserIdentity().getCreatedBy(), "Valid user identification for user performing this action is required");
            if (this.financierType == FinancierType.INDIVIDUAL) {
                validateUserIdentity();
            } else {
                validateCooperation();
            }
        }
    }

    private void validateCooperation() throws MeedlException {
        MeedlValidator.validateObjectInstance(cooperation, UserMessages.COOPERATION_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateObjectName(this.cooperation.getName(), " name cannot be empty", "Cooperation");
        MeedlValidator.validateObjectInstance(this.userIdentity, UserMessages.USER_IDENTITY_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateEmail(this.userIdentity.getEmail());
    }
    private void validationBeforeKycProcessing() throws MeedlException {
        MeedlValidator.validateObjectInstance(this.userIdentity, "User performing this action is unknown");
        MeedlValidator.validateUUID(this.userIdentity.getId(), "User identification performing this action is unknown. ");
        .kjhbhgbh
    }

    public void validateKyc() throws MeedlException {
        MeedlValidator.validateUUID(id, FinancierMessages.INVALID_FINANCIER_ID.getMessage());
        MeedlValidator.validateObjectInstance(userIdentity.getBankDetail(), "Provide a valid bank detail.");
        MeedlValidator.validateUUID(userIdentity.getBankDetail().getId(), "Provide a valid bank detail id.");
        userIdentity.getBankDetail().validate();
        MeedlValidator.validateDataElement(userIdentity.getNin(), "Nin is required");
        MeedlValidator.validateDataElement(userIdentity.getTaxId(), "Tax id is required");
    }
    public void validateFinancierDesignation() throws MeedlException {
        MeedlValidator.validateObjectInstance(this.investmentVehicleDesignation, FinancierMessages.FINANCIER_DESIGNATION_REQUIRED.getMessage());
        MeedlValidator.validateCollection(this.investmentVehicleDesignation, FinancierMessages.FINANCIER_DESIGNATION_REQUIRED.getMessage());
        if ((this.investmentVehicleDesignation.contains(InvestmentVehicleDesignation.DONOR) ||
                this.investmentVehicleDesignation.contains(InvestmentVehicleDesignation.ENDOWER) ||
                this.investmentVehicleDesignation.contains(InvestmentVehicleDesignation.INVESTOR)) &&
                this.investmentVehicleDesignation.size() > BigInteger.ONE.intValue()
        ){
            log.error("Investment vehicle designation for financier --- Designation(s) : {}", this.investmentVehicleDesignation);
            throw new MeedlException("Financier can only be assigned a single role.");
        }
    }

}
