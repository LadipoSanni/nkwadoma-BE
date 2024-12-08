package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanRequestMapper {
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
    LoanRequest mapProjectionToLoanRequest(LoanRequestProjection loanRequestProjection);

    @Mapping(target = "cohortId", source = "loanee.cohortId")
    @Mapping(target = "loanReferralId", source = "id")
    @Mapping(target = "loanee.id", source = "loanee.id")
    @Mapping(target = "createdDate", expression = "java(java.time.LocalDateTime.now())")
    LoanRequest mapLoanReferralToLoanRequest(LoanReferral updatedLoanReferral);
}
