package africa.nkwadoma.nkwadoma.domain.model.financier;

import africa.nkwadoma.nkwadoma.domain.enums.AccreditationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleDesignation;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleType;
import africa.nkwadoma.nkwadoma.domain.exceptions.InvestmentException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.Cooperation;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;
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
    private String tin;
    private String name;
    private String bankDetailId;

    private FinancierType financierType;
    private ActivationStatus activationStatus;
    private AccreditationStatus accreditationStatus;
    private String investmentVehicleId;
    private BigDecimal amountToInvest;
    private BigDecimal totalAmountInvested;
    private Cooperation cooperation;
    private String identity;
    private UserIdentity userIdentity;
    private OrganizationIdentity organizationIdentity;
    private int totalNumberOfInvestment;
    private int pageNumber;
    private int pageSize;
    private BigDecimal totalIncomeEarned;
    private BigDecimal portfolioValue;
    private String rcNumber;
    private LocalDateTime createdAt;
    private String actorId;
    private String cooperateId;
    private InvestmentVehicleType investmentVehicleType;
    private String investmentId;
    private String investmentVehicleName;
    private BigDecimal amountInvested;
    private LocalDate dateInvested;
    private BigDecimal incomeEarned;
    private BigDecimal netAssertValue;
    private String email;
    private String invitedBy;
    private String cooperateAdminEmail;
    private String cooperateAdminName;

    private List<BankDetail> bankDetails;
    private Set<InvestmentVehicleDesignation> investmentVehicleDesignation;
    private List<FinancierVehicleDetail> investmentVehicleInvestedIn;
    private List<BeneficialOwner> beneficialOwners;
    private List<InvestmentVehicle> investmentVehicles;

    //source of fund
    private Set<String> sourceOfFunds;

    private String taxInformationNumber;


    //Declaration
    private boolean declarationAndAgreement;
    private boolean politicallyExposed;
    private boolean privacyPolicyAccepted;
    private List<PoliticallyExposedPerson> politicallyExposedPeople;

    private void validateUserIdentity() throws InvestmentException, MeedlException {
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
            throw new InvestmentException(e.getMessage() + " for : "+ userIdentity.getEmail());
        }
    }

    public void validate() throws InvestmentException, MeedlException {
        log.info("Validating financier details to save.");
        if (MeedlValidator.isNotValidId(this.id)) {
            MeedlValidator.validateObjectInstance(this.financierType, FinancierMessages.INVALID_FINANCIER_TYPE.getMessage());
            MeedlValidator.validateObjectInstance(this.getUserIdentity(), UserMessages.USER_IDENTITY_MUST_NOT_BE_EMPTY.getMessage());
            MeedlValidator.validateUUID(this.getUserIdentity().getCreatedBy(), "Valid user identification for user performing this action is required");
            validateUserIdentity();
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
        MeedlValidator.validateObjectName(this.cooperation.getName(), "name cannot be empty", "Cooperation");
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
//        validateBeneficialOwnersKyc();
        if (beneficialOwners != null){
            validateBeneficialOwnersPercentageOwnershipOrShare();
        }
    }

    private void validateBeneficialOwnersPercentageOwnershipOrShare() throws MeedlException {
        double totalPercentage = beneficialOwners.stream()
                .mapToDouble(BeneficialOwner::getPercentageOwnershipOrShare)
                .sum();

        if (totalPercentage < 100.0) {
            throw new MeedlException("Total ownership percentage cannot be less than 100%. Found: " + totalPercentage + "%");
        }

        if (totalPercentage > 100.0) {
            throw new MeedlException("Total ownership percentage cannot be greater than 100%. Found: " + totalPercentage + "%");
        }
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
                MeedlValidator.validateCollection(this.politicallyExposedPeople, "No politically exposed person provided");
                for (PoliticallyExposedPerson politicallyExposedPerson : this.politicallyExposedPeople) {
                    log.info("Politically exposed person {}", politicallyExposedPerson);
                    MeedlValidator.validateObjectInstance(politicallyExposedPerson, "Political politicallyExposedPerson exposed to should be declared.");
                    politicallyExposedPerson.validate();
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
        for (String sourceOfFund : this.sourceOfFunds) {
            if (MeedlValidator.isEmptyString(sourceOfFund)){
                log.warn("Source of fund not provided");
                throw new MeedlException(sourceOfFundErrorMessage);
            }
            if (sourceOfFund.length() > 200) {
                log.error("Source of fund cannot be greater than 200 characters. {}", sourceOfFund.length());
                throw new MeedlException("Source of fund cannot be greater than 200 characters.");
            }
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
