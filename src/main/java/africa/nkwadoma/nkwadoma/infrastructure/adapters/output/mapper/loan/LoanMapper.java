package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanDetailSummary;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.math.BigDecimal;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanMapper {
    LoanEntity mapToLoanEntity(Loan loan);

    @InheritInverseConfiguration
    Loan mapToLoan(LoanEntity loanEntity);

    @Mapping(target = "loanAmountRequested", source = "loanAmountRequested")
    @Mapping(target = "loaneeId", source = "loaneeId")
    @Mapping(target = "cohortId", source = "cohortId")
    @Mapping(target = "nextOfKin.id", source = "nextOfKinId")
    @Mapping(target = "nextOfKin.firstName", source = "nextOfKinFirstName")
    @Mapping(target = "nextOfKin.lastName", source = "nextOfKinLastName")
    @Mapping(target = "nextOfKin.email", source = "nextOfKinEmail")
    @Mapping(target = "nextOfKin.phoneNumber", source = "nextOfKinPhoneNumber")
    @Mapping(target = "nextOfKin.contactAddress", source = "nextOfKinContactAddress")
    @Mapping(target = "nextOfKin.nextOfKinRelationship", source = "nextOfKinRelationship")
    @Mapping(target = "userIdentity.firstName", source = "firstName")
    @Mapping(target = "userIdentity.lastName", source = "lastName")
    @Mapping(target = "userIdentity.email", source = "email")
    @Mapping(target = "userIdentity.gender", source = "gender")
    @Mapping(target = "userIdentity.phoneNumber", source = "phoneNumber")
    @Mapping(target = "userIdentity.image", source = "loaneeImage")
    @Mapping(target = "userIdentity.dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "userIdentity.nationality", source = "nationality")
    @Mapping(target = "userIdentity.stateOfResidence", source = "stateOfResidence")
    @Mapping(target = "userIdentity.stateOfOrigin", source = "stateOfOrigin")
    @Mapping(target = "userIdentity.maritalStatus", source = "maritalStatus")
    @Mapping(target = "userIdentity.residentialAddress", source = "residentialAddress")
    @Mapping(target = "userIdentity.alternateEmail", source = "alternateEmail")
    @Mapping(target = "userIdentity.alternatePhoneNumber", source = "alternatePhoneNumber")
    @Mapping(target = "userIdentity.alternateContactAddress", source = "alternateContactAddress")
    @Mapping(target = "loanAmountApproved", source = "loanAmountApproved")
    @Mapping(target = "tuitionAmount", source = "tuitionAmount")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "cohortLoaneeId", source = "cohortLoaneeId")
    @Mapping(target = "organizationName", source = "referredBy")
    @Mapping(target = "loanAmountOutstanding", source = "amountOutstanding")
    @Mapping(target = "loanAmountRepaid", source = "amountRepaid")
    @Mapping(target = "interestRate", source = "interestRate")
    @Mapping(target = "interestIncurred", source = "interestIncurred")
    Loan mapProjectionToLoan(LoanProjection loanProjection);

    LoanDetailSummary toLoanDetailSummary(LoanSummaryProjection loanSummaryProjection);

}
