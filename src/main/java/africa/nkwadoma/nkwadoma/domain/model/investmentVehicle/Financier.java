package africa.nkwadoma.nkwadoma.domain.model.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.AccreditationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.enums.identity.Country;
import africa.nkwadoma.nkwadoma.domain.enums.identity.UserRelationship;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    //source of fund
    private String personalOrJointSavings;
    private String employmentIncome;
    private String salesOfAssets;
    private String donation;
    private String inheritanceOrGift;
    private String compensationOfLegalSettlements;
    private BigDecimal profitFromLegitimateActivities;
    private String occupation;

    //Beneficial owner information
    private FinancierType beneficialOwnerType;
    //beneficial Entity
    private String entityName;
    private String beneficialRcNumber;
    private String taxInformationNumber;
    private Country countryOfIncorporation;

    //beneficial individual
    private String beneficialOwnerFirstName;
    private String beneficialOwnerLastName;
    private UserRelationship beneficialOwnerRelationship;
    private LocalDate beneficialOwnerDateOfBirth;
    private double percentageOwnershipOrShare;
//    Gov ID
    private String votersCard;
    private String nationalIdCard;
    private String driverLicensetionalIdCard;
    private String driverLicense;

    //Declaration
    private boolean declarationAndAgreement;
    private boolean politicallyExposed;
    private List<PoliticalPartyExposedTo> politicalPartiesExposedTo;

    private void validateUserIdentity() throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, UserMessages.USER_IDENTITY_MUST_NOT_BE_EMPTY.getMessage());
        validateFinancierEmail(userIdentity);
        MeedlValidator.validateDataElement(userIdentity.getFirstName(), UserMessages.INVALID_FIRST_NAME.getMessage());
        MeedlValidator.validateDataElement(userIdentity.getLastName(), UserMessages.INVALID_LAST_NAME.getMessage());
        MeedlValidator.validateEmail(userIdentity.getEmail());
        MeedlValidator.validateObjectInstance(userIdentity, UserMessages.USER_IDENTITY_MUST_NOT_BE_EMPTY.getMessage());
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
            if (financierIsIndividual()) {
                validateUserIdentity();
            } else {
                validateCooperation();
            }
        }
    }
    private boolean financierIsIndividual(){
        return this.financierType == FinancierType.INDIVIDUAL;
    }

    private void validateCooperation() throws MeedlException {
        MeedlValidator.validateObjectInstance(cooperation, UserMessages.COOPERATION_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateObjectName(this.cooperation.getName(), " name cannot be empty", "Cooperation");
        MeedlValidator.validateObjectInstance(this.userIdentity, UserMessages.USER_IDENTITY_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateEmail(this.userIdentity.getEmail());
    }
    public void validateKyc() throws MeedlException {
        MeedlValidator.validateObjectInstance(this.userIdentity, "User performing this action is unknown.");
        MeedlValidator.validateObjectInstance(this.userIdentity.getId(), "Identification for user performing this action is unknown.");
        MeedlValidator.validateObjectInstance(this.userIdentity.getBankDetail(), "Provide a valid bank detail.");
        this.userIdentity.getBankDetail().validate();
        MeedlValidator.validateDataElement(this.userIdentity.getPhoneNumber(), "Phone number is required.");
        if (financierIsIndividual()){
            MeedlValidator.validateDataElement(this.occupation, "Occupation is required.");
            validateKycIdentityNumbers();
        }else {
            MeedlValidator.validateDataElement(this.taxInformationNumber, "Tax information number is required.");
            MeedlValidator.validateDataElement(this.beneficialRcNumber, "Rc number is required.");
        }
        validateSourceOfFund();
        validateDeclaration();
        validateBeneficialOwnerKyc();
    }
    private void validateBeneficialOwnerKyc() throws MeedlException {
        if (beneficialOwnerType != null){
            log.info("Beneficial own type stated {}, validations begin for beneficial own with this type.", beneficialOwnerType);
//            validateProofOfBeneficialOwnership();
            if (this.beneficialOwnerType == FinancierType.INDIVIDUAL){
                MeedlValidator.validateDataElement(this.beneficialOwnerFirstName, "Beneficial owner first name is required.");
                MeedlValidator.validateDataElement(this.beneficialOwnerLastName, "Beneficial owner last name is required.");
                MeedlValidator.validateObjectInstance(this.beneficialOwnerRelationship, "Beneficial owner relationship is required.");
                MeedlValidator.validateObjectInstance(this.beneficialOwnerDateOfBirth, "Beneficial owner date of birth is required.");
                MeedlValidator.validateDoubleDataElement(this.percentageOwnershipOrShare, "Beneficial owner percentage ownership or share is required.");
            }{
                MeedlValidator.validateDataElement(this.entityName, "Entity name is required.");
                MeedlValidator.validateRCNumber(this.beneficialRcNumber);
                MeedlValidator.validateObjectInstance(this.countryOfIncorporation, "Country of incorporation is required.");
            }
        }
    }

    private void validateProofOfBeneficialOwnership() throws MeedlException {
        log.info("votersCard {} , nationalIdCard {}, driverLicensetionalIdCard {}, driverLicense {}",votersCard, nationalIdCard, driverLicensetionalIdCard, driverLicense);
        if (MeedlValidator.isNotEmptyString(this.votersCard) ||
            MeedlValidator.isNotEmptyString(this.nationalIdCard) ||
            MeedlValidator.isNotEmptyString(this.driverLicensetionalIdCard) ||
            MeedlValidator.isNotEmptyString(this.driverLicense)){
            log.info("Proof of beneficial ownership was provided. At least one was given.");
        }{
            throw new MeedlException("Please provide at least one. Voters card/national id card/drivers licensetionak id card/driver license.");
        }

    }

    private void validateDeclaration() throws MeedlException {
        if (this.declarationAndAgreement){
            if (this.isPoliticallyExposed()){
                for (PoliticalPartyExposedTo party : this.politicalPartiesExposedTo) {
                    MeedlValidator.validateObjectInstance(party, "Political party exposed to should be declared.");
                    party.validate();
                }
            }
        }else {
            throw new MeedlException("Please agree to the declaration and agreement.");
        }
    }
    private void validateSourceOfFund() throws MeedlException {
        MeedlValidator.validateDataElement(this.personalOrJointSavings, "Personal or joint savings needs to be stated.");
        MeedlValidator.validateDataElement(this.employmentIncome, "Employment income needs to be stated.");
        MeedlValidator.validateDataElement(this.salesOfAssets, "Sales of assets needs to be stated.");
        MeedlValidator.validateDataElement(this.donation, "Donation needs to be stated.");
        MeedlValidator.validateDataElement(this.occupation, "Occupation needs to be stated.");
        MeedlValidator.validateDataElement(this.inheritanceOrGift, "Inheritance or gift needs to be stated.");
        MeedlValidator.validateDataElement(this.compensationOfLegalSettlements, "Compensation of legal settlements needs to be stated.");
        MeedlValidator.validateObjectInstance(this.profitFromLegitimateActivities, "Profit From Legitimate Activities of legal settlements needs to be stated.");
    }

    private void validateKycIdentityNumbers() throws MeedlException {
        MeedlValidator.validateDataElement(userIdentity.getNin(), "Nin is required");
        MeedlValidator.validateDataElement(userIdentity.getTaxId(), "Tax id is required");
        MeedlValidator.validateDataElement(userIdentity.getBvn(), "Bvn is required");
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
