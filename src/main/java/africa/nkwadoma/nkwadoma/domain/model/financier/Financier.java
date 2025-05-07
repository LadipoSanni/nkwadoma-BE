package africa.nkwadoma.nkwadoma.domain.model.financier;

import africa.nkwadoma.nkwadoma.domain.enums.AccreditationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.enums.identity.Country;
import africa.nkwadoma.nkwadoma.domain.enums.identity.UserRelationship;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleDesignation;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleType;
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
import org.apache.commons.lang3.StringUtils;

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
public class    Financier {
    private String id;
    private List<BeneficialOwner> beneficialOwners;

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
    private String actorId;
    private InvestmentVehicleType investmentVehicleType;
    private String investmentId;
    private String investmentVehicleName;
    private BigDecimal amountInvested;
    private LocalDate dateInvested;
    private BigDecimal incomeEarned;
    private BigDecimal netAssertValue;

    //source of fund
    private Set<String> sourceOfFunds;

    private String taxInformationNumber;


    //Declaration
    private boolean declarationAndAgreement;
    private boolean politicallyExposed;
    private List<PoliticalPartyExposedTo> politicalPartiesExposedTo;

    private void validateUserIdentity() throws MeedlException {
        log.info("Started validating financier user identity.");
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
        log.info("Validating financier details to save.");
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
        log.info("Financier is an individual {}", this.financierType == FinancierType.INDIVIDUAL);
        return this.financierType == FinancierType.INDIVIDUAL;
    }
    private boolean financierIsIndividual(FinancierType financierType) {
        log.info("Financier is an individual {}", financierType == FinancierType.INDIVIDUAL);
        return financierType == FinancierType.INDIVIDUAL;
    }

    private void validateCooperation() throws MeedlException {
        log.info("Started cooperation validation in financier");
        MeedlValidator.validateObjectInstance(cooperation, UserMessages.COOPERATION_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateObjectName(this.cooperation.getName(), " name cannot be empty", "Cooperation");
        MeedlValidator.validateObjectInstance(this.userIdentity, UserMessages.USER_IDENTITY_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateEmail(this.userIdentity.getEmail());
    }
    public void validateKyc(FinancierType financierType) throws MeedlException {
        log.info("Started kyc validation in financier.");
        MeedlValidator.validateObjectInstance(this.userIdentity, UserMessages.NULL_ACTOR_USER_IDENTITY.getMessage());
        MeedlValidator.validateObjectInstance(this.userIdentity.getId(), "Identification for user performing this action is unknown.");
//        MeedlValidator.validateObjectInstance(this.userIdentity.getBankDetail(), "Provide a valid bank detail.");
//        this.userIdentity.getBankDetail().validate();
        MeedlValidator.validateDataElement(this.userIdentity.getPhoneNumber(), "Phone number is required.");
        if (financierIsIndividual(financierType)){
            log.info("Validating individual financier for kyc");
//            MeedlValidator.validateDataElement(this.occupation, "Occupation is required.");
            validateKycIdentityNumbers();
        }
        else {
            MeedlValidator.validateDataElement(this.taxInformationNumber, "Tax information number is required.");
            MeedlValidator.validateRCNumber(this.rcNumber);
        }
        validateSourceOfFund();
        validateDeclaration();
        validateBeneficialOwnersKyc();
    }

    private void validateBeneficialOwnersKyc() throws MeedlException {
        log.info("Validating beneficial owners.");
        MeedlValidator.validateObjectInstance(beneficialOwners, "Please provide beneficial owner.");
        for (BeneficialOwner beneficialOwner : beneficialOwners){
            beneficialOwner.validate();
        }
    }

    private void validateDeclaration() throws MeedlException {
        if (this.declarationAndAgreement){
            if (this.isPoliticallyExposed()){
                MeedlValidator.validateCollection(this.politicalPartiesExposedTo, "Political party exposed to should be declared.");
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
        String sourceOfFundErrorMessage = "Source of fund cannot be empty";
        MeedlValidator.validateObjectInstance(this.sourceOfFunds, sourceOfFundErrorMessage);
        MeedlValidator.validateCollection(this.sourceOfFunds, sourceOfFundErrorMessage);
        boolean atLeastOneSourceOfFund = Boolean.FALSE;
        for (String sourceOfFund : this.sourceOfFunds) {
            if (MeedlValidator.isNotEmptyString(sourceOfFund)) {
                atLeastOneSourceOfFund = Boolean.TRUE;
                log.info("At least one source of fund was provided ----- {}", sourceOfFund);
                break;
            }
        }
        if (!atLeastOneSourceOfFund){
            log.warn("Source of fund not provided");
            throw new MeedlException(sourceOfFundErrorMessage);
        }
//        MeedlValidator.validateDataElement(this.personalOrJointSavings, "Personal or joint savings needs to be stated.");
//        MeedlValidator.validateDataElement(this.employmentIncome, "Employment income needs to be stated.");
//        MeedlValidator.validateDataElement(this.salesOfAssets, "Sales of assets needs to be stated.");
//        MeedlValidator.validateDataElement(this.donation, "Donation needs to be stated.");
//        MeedlValidator.validateDataElement(this.occupation, "Occupation needs to be stated.");
//        MeedlValidator.validateDataElement(this.inheritanceOrGift, "Inheritance or gift needs to be stated.");
//        MeedlValidator.validateDataElement(this.compensationOfLegalSettlements, "Compensation of legal settlements needs to be stated.");
//        MeedlValidator.validateObjectInstance(this.profitFromLegitimateActivities, "Profit From Legitimate Activities of legal settlements needs to be stated.");
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
