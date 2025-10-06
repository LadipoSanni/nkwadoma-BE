package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanRequestMapper {
//    @Mapping(target = "loaneeEntity", source = "loanee")
    LoanRequestEntity toLoanRequestEntity(LoanRequest loanRequest);

    @InheritInverseConfiguration
    LoanRequest toLoanRequest(LoanRequestEntity loanRequestEntity);

    @Mapping(target = "nextOfKin.id", source = "nextOfKinId")
    @Mapping(target = "nextOfKin.firstName", source = "nextOfKinFirstName")
    @Mapping(target = "nextOfKin.lastName", source = "nextOfKinLastName")
    @Mapping(target = "nextOfKin.email", source = "nextOfKinEmail")
    @Mapping(target = "nextOfKin.phoneNumber", source = "nextOfKinPhoneNumber")
    @Mapping(target = "nextOfKin.contactAddress", source = "nextOfKinContactAddress")
    @Mapping(target = "nextOfKin.nextOfKinRelationship", source = "nextOfKinRelationship")
    @Mapping(target = "userIdentity.firstName", source = "firstName")
    @Mapping(target = "userIdentity.lastName", source = "lastName")
    @Mapping(target = "userIdentity.phoneNumber", source = "phoneNumber")
    @Mapping(target = "userIdentity.image", source = "loaneeImage")
    @Mapping(target = "userIdentity.email", source = "email")
    @Mapping(target = "userIdentity.gender", source = "gender")
    @Mapping(target = "userIdentity.dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "userIdentity.nationality", source = "nationality")
    @Mapping(target = "userIdentity.stateOfResidence", source = "stateOfResidence")
    @Mapping(target = "userIdentity.stateOfOrigin", source = "stateOfOrigin")
    @Mapping(target = "userIdentity.maritalStatus", source = "maritalStatus")
    @Mapping(target = "userIdentity.residentialAddress", source = "residentialAddress")
    @Mapping(target = "userIdentity.alternateEmail", source = "alternateEmail")
    @Mapping(target = "userIdentity.alternatePhoneNumber", source = "alternatePhoneNumber")
    @Mapping(target = "userIdentity.alternateContactAddress", source = "alternateContactAddress")
    @Mapping(target = "loanAmountRequested", source = "loanAmountRequested")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "onboardingMode", source = "onboardingMode")
    @Mapping(target = "isVerified", source = "isVerified")
    @Mapping(target = "cohortLoaneeId", source = "cohortLoaneeId")
    @Mapping(target = "cohortId", source = "cohortId")
    @Mapping(target = "loaneeId", source = "loaneeId")
    LoanRequest mapProjectionToLoanRequest(LoanRequestProjection loanRequestProjection);

    @Mapping(target = "loanAmountRequested", source = "cohortLoanee.loaneeLoanDetail.amountRequested")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "referredBy", source = "cohortLoanee.referredBy")
    LoanRequest mapLoanReferralToLoanRequest(LoanReferral updatedLoanReferral);

    @Mapping(target = "dateTimeApproved", expression = "java(java.time.LocalDateTime.now())")
    LoanRequest updateLoanRequest(LoanRequest updatedLoanRequest, @MappingTarget LoanRequest foundLoanRequest);
}
