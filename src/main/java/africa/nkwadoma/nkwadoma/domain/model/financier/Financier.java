package africa.nkwadoma.nkwadoma.domain.model.financier;

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
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Cooperation;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Slf4j
@Getter
@Setter
@ToString
@Builder
public class Financier {
    private String id;
    private FinancierType financierType;
    private ActivationStatus activationStatus;
    private AccreditationStatus accreditationStatus;
    private String investmentVehicleId;
    private BigDecimal amountToInvest;
    private BigDecimal totalAmountInvested;
    private Set<InvestmentVehicleDesignation> investmentVehicleDesignation;
    private Cooperation cooperation;
    private UserIdentity userIdentity;
    private int totalNumberOfInvestment;
    private int pageNumber;
    private int pageSize;
    private BigDecimal totalIncomeEarned;
    private BigDecimal portfolioValue;
    private List<FinancierVehicleDetail> investmentVehicleInvestedIn;
    private List<InvestmentVehicle> investmentVehicles;
    private String rcNumber;
    private LocalDateTime createdAt;

    //source of fund
    private String personalOrJointSavings;
    private String employmentIncome;
    private String salesOfAssets;
    private String donation;
    private String inheritanceOrGift;
    private String compensationOfLegalSettlements;
    private BigDecimal profitFromLegitimateActivities;
    private String occupation;
    private String taxInformationNumber;

    private List<BeneficialOwner> beneficialOwners;

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
        MeedlValidator.validateObjectInstance(this.userIdentity, UserMessages.NULL_ACTOR_USER_IDENTITY.getMessage());
        MeedlValidator.validateObjectInstance(this.userIdentity.getId(), "Identification for user performing this action is unknown.");
        MeedlValidator.validateObjectInstance(this.userIdentity.getBankDetail(), "Provide a valid bank detail.");
        this.userIdentity.getBankDetail().validate();
        MeedlValidator.validateDataElement(this.userIdentity.getPhoneNumber(), "Phone number is required.");
        if (financierIsIndividual()){
            MeedlValidator.validateDataElement(this.occupation, "Occupation is required.");
            validateKycIdentityNumbers();
        }
        else {
            MeedlValidator.validateDataElement(this.taxInformationNumber, "Tax information number is required.");
            MeedlValidator.validateDataElement(this.rcNumber, "Rc number is required.");
        }
        validateSourceOfFund();
        validateDeclaration();
        validateBeneficialOwnersKyc();
    }
    private void validateBeneficialOwnersKyc() throws MeedlException {
        MeedlValidator.validateObjectInstance(beneficialOwners, "Please provide beneficial owner.");
        for (BeneficialOwner beneficialOwner : beneficialOwners){
            validateBeneficialOwnerKyc(beneficialOwner);
        }
    }
    private void validateBeneficialOwnerKyc(BeneficialOwner beneficialOwner) throws MeedlException {
        if (beneficialOwner.getBeneficialOwnerType() != null){
            log.info("Beneficial own type stated {}, validations begin for beneficial own with this type.", beneficialOwner.getBeneficialOwnerType());
            validateProofOfBeneficialOwnership(beneficialOwner);
            if (beneficialOwner.getBeneficialOwnerType() == FinancierType.INDIVIDUAL){
                MeedlValidator.validateDataElement(beneficialOwner.getBeneficialOwnerFirstName(), "Beneficial owner first name is required.");
                MeedlValidator.validateDataElement(beneficialOwner.getBeneficialOwnerLastName(), "Beneficial owner last name is required.");
                MeedlValidator.validateObjectInstance(beneficialOwner.getBeneficialOwnerRelationship(), "Beneficial owner relationship is required.");
                MeedlValidator.validateObjectInstance(beneficialOwner.getBeneficialOwnerDateOfBirth(), "Beneficial owner date of birth is required.");
                MeedlValidator.validateDoubleDataElement(beneficialOwner.getPercentageOwnershipOrShare(), "Beneficial owner percentage ownership or share is required.");
            }{
                MeedlValidator.validateDataElement(beneficialOwner.getEntityName(), "Entity name is required.");
                MeedlValidator.validateRCNumber(beneficialOwner.getBeneficialRcNumber());
                MeedlValidator.validateObjectInstance(beneficialOwner.getCountryOfIncorporation(), "Country of incorporation is required.");
            }
        }
    }
    public void validateProofOfBeneficialOwnership(BeneficialOwner beneficialOwner) throws MeedlException {
        if (isBlank(beneficialOwner.getVotersCard()) && isBlank(beneficialOwner.getNationalIdCard()) && isBlank(beneficialOwner.getDriverLicense()) && isBlank(beneficialOwner.getDriverLicensetionalIdCard())) {
            throw new MeedlException("At least one form of beneficial owner identification must be provided.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
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

    public void validateInvestInVehicleDetails() throws MeedlException {
        MeedlValidator.validateObjectInstance(this.userIdentity, UserMessages.NULL_ACTOR_USER_IDENTITY.getMessage());
        MeedlValidator.validateUUID(this.userIdentity.getId(), UserMessages.INVALID_USER_ID.getMessage());
        MeedlValidator.validateBigDecimalDataElement(this.amountToInvest, FinancierMessages.AMOUNT_TO_INVEST_REQUIRED.getMessage());
    }
}
