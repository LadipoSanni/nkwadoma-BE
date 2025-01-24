package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanRequestMapper {
    @Mapping(target = "loaneeEntity", source = "loanee")
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
    @Mapping(target = "userIdentity.image", source = "loaneeImage")
    @Mapping(target = "userIdentity.email", source = "email")
    @Mapping(target = "userIdentity.gender", source = "gender")
    @Mapping(target = "userIdentity.dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "userIdentity.nationality", source = "nationality")
    @Mapping(target = "userIdentity.stateOfResidence", source = "stateOfResidence")
    @Mapping(target = "userIdentity.stateOfOrigin", source = "stateOfOrigin")
    @Mapping(target = "userIdentity.maritalStatus", source = "maritalStatus")
    @Mapping(target = "userIdentity.residentialAddress", source = "residentialAddress")
    @Mapping(target = "loanAmountRequested", source = "loanAmountRequested")
    @Mapping(target = "status", source = "status")
    LoanRequest mapProjectionToLoanRequest(LoanRequestProjection loanRequestProjection);

    @Mapping(target = "loanAmountRequested", source = "loanee.loaneeLoanDetail.amountRequested")
    @Mapping(target = "cohortId", source = "loanee.cohortId")
    @Mapping(target = "loanReferralId", source = "id")
    @Mapping(target = "loanee.id", source = "loanee.id")
    @Mapping(target = "createdDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "dateTimeApproved", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "referredBy", source = "updatedLoanReferral.loanee.referredBy")
    @Mapping(target = "loaneeId", source = "loanee.id")
    LoanRequest mapLoanReferralToLoanRequest(LoanReferral updatedLoanReferral);

    LoanRequest updateLoanRequest(LoanRequest updatedLoanRequest, @MappingTarget LoanRequest foundLoanRequest);
}
