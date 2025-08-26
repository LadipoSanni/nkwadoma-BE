package africa.nkwadoma.nkwadoma.domain.model.financier;

import africa.nkwadoma.nkwadoma.domain.enums.identity.Country;
import africa.nkwadoma.nkwadoma.domain.enums.identity.UserRelationship;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class BeneficialOwner {
    private String id;
    //Beneficial owner information
    private FinancierType beneficialOwnerType;
    //beneficial Entity
    private String entityName;
    private String beneficialRcNumber;
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
    private String driverLicense;

    public void validate() throws MeedlException {
        log.warn("Started validating at the beneficial owner object");
        if (this.getBeneficialOwnerType() != null){
            log.info("Beneficial own type stated {}, validations begin for beneficial own with this type.", this.getBeneficialOwnerType());
            if (this.getBeneficialOwnerType() == FinancierType.INDIVIDUAL){
                validateProofOfBeneficialOwnership(this);
                MeedlValidator.validateDataElement(this.getBeneficialOwnerFirstName(), "Beneficial owner first name is required.");
                MeedlValidator.validateDataElement(this.getBeneficialOwnerLastName(), "Beneficial owner last name is required.");
                MeedlValidator.validateObjectInstance(this.getBeneficialOwnerRelationship(), "Beneficial owner relationship is required.");
                MeedlValidator.validateObjectInstance(this.beneficialOwnerDateOfBirth, "Beneficial owner date of birth is required.");
                MeedlValidator.validateDoubleDataElement(this.getPercentageOwnershipOrShare(), "Beneficial owner percentage ownership or share is required.");
            }else {
                MeedlValidator.validateDataElement(this.getEntityName(), "Entity name is required.");
                MeedlValidator.validateRCNumber(this.getBeneficialRcNumber());
                MeedlValidator.validateObjectInstance(this.getCountryOfIncorporation(), "Country of incorporation is required.");
            }
            if (this.getPercentageOwnershipOrShare() < 0) {
                throw new MeedlException("Beneficial owner percentage ownership or share cannot be negative.");
            }
            if (this.getPercentageOwnershipOrShare() > 100) {
                throw new MeedlException("Beneficial owner percentage ownership or share cannot be greater than 100.");
            }
        }
    }
    public void validateProofOfBeneficialOwnership(BeneficialOwner beneficialOwner) throws MeedlException {
        if (MeedlValidator.isEmptyString(beneficialOwner.getVotersCard()) && MeedlValidator.isEmptyString(beneficialOwner.getNationalIdCard()) && MeedlValidator.isEmptyString(beneficialOwner.getDriverLicense())) {
            throw new MeedlException("At least one form of beneficial owner identification must be provided.");
        }
    }

}
