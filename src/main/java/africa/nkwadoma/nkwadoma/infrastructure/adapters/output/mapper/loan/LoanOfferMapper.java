package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan;


import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanOfferEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanOfferProjection;
import org.mapstruct.*;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanOfferMapper {
    @Mapping(target = "dateTimeOffered", expression = "java(java.time.LocalDateTime.now())")
    LoanOfferEntity toLoanOfferEntity(LoanOffer loanOffer);

    @InheritInverseConfiguration
    LoanOffer toLoanOffer(LoanOfferEntity loanOfferEntity);

    void updateLoanOffer(@MappingTarget LoanOffer offer, LoanOffer loanOffer);

    @Mapping(target = "nextOfKin.firstName", source = "nextOfKinFirstName")
    @Mapping(target = "nextOfKin.lastName", source = "nextOfKinLastName")
    @Mapping(target = "nextOfKin.email", source = "nextOfKinEmail")
    @Mapping(target = "nextOfKin.phoneNumber", source = "nextOfKinPhoneNumber")
    @Mapping(target = "nextOfKin.contactAddress", source = "nextOfKinContactAddress")
    @Mapping(target = "nextOfKin.nextOfKinRelationship", source = "nextOfKinRelationship")
    @Mapping(target = "userIdentity.image", source = "loaneeImage")
    @Mapping(target = "userIdentity.gender", source = "gender")
    @Mapping(target = "userIdentity.dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "userIdentity.nationality", source = "nationality")
    @Mapping(target = "userIdentity.stateOfResidence", source = "stateOfResidence")
    @Mapping(target = "userIdentity.stateOfOrigin", source = "stateOfOrigin")
    @Mapping(target = "userIdentity.maritalStatus", source = "maritalStatus")
    @Mapping(target = "userIdentity.firstName", source = "firstName")
    @Mapping(target = "userIdentity.lastName", source = "lastName")
    @Mapping(target = "userIdentity.phoneNumber", source = "phoneNumber")
    @Mapping(target = "userIdentity.email", source = "email")
    @Mapping(target = "userIdentity.residentialAddress", source = "residentialAddress")
    @Mapping(target = "userIdentity.alternateEmail", source = "alternateEmail")
    @Mapping(target = "userIdentity.alternatePhoneNumber", source = "alternatePhoneNumber")
    @Mapping(target = "userIdentity.alternateContactAddress", source = "alternateContactAddress")
    @Mapping(target = "loaneeLoanDetail.amountRequested", source = "amountRequested")
    @Mapping(target = "loaneeLoanDetail.initialDeposit", source = "initialDeposit")
    @Mapping(target = "loaneeLoanDetail.tuitionAmount", source = "tuitionAmount")
    @Mapping(target = "loaneeLoanDetail.amountApproved", source = "amountApproved")
    @Mapping(target = "loaneeId", source = "loaneeId")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "loanProduct.name", source = "loanProductName")
    @Mapping(target = "dateTimeOffered", source = "dateTimeOffered")
    @Mapping(target = "programName", source = "programName")
    @Mapping(target = "cohortName", source = "cohortName")
    @Mapping(target = "termsAndCondition", source = "termsAndCondition")
    @Mapping(target = "loanRequest.id", source = "loanRequestId")
    @Mapping(target = "loanProduct.id", source = "loanProductId")
    @Mapping(target = "loanee.id", source = "loaneeId")
    @Mapping(target = "loanOfferStatus", source = "loanOfferStatus")
    @Mapping(target = "loaneeResponse", source = "loaneeResponse")
    @Mapping(target = "organizationId", source = "organizationId")
    @Mapping(target = "cohortId", source = "cohortId")
    @Mapping(target = "referredBy", source = "referredBy")
    @Mapping(target = "cohortLoaneeId", source = "cohortLoaneeId")
    @Mapping(target = "levelOfEducation", source = "levelOfEducation")
    LoanOffer mapProjectionToLoanOffer(LoanOfferProjection loanOfferProjection);

}
