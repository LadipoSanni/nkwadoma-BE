package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.education;


import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortLoaneeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortLoaneeProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CohortLoaneeMapper {

    CohortLoaneeEntity toCohortLoaneeEntity(CohortLoanee cohortLoanee);

    CohortLoanee toCohortLoanee(CohortLoaneeEntity cohortLoaneeEntity);


    @Mapping(target = "loanee.userIdentity.firstName", source = "firstName")
    @Mapping(target = "loanee.userIdentity.lastName", source = "lastName")
    @Mapping(target = "loanee.userIdentity.email", source = "email")
    @Mapping(target = "loanee.userIdentity.gender", source = "gender")
    @Mapping(target = "loanee.id", source = "loaneeId")
    @Mapping(target = "loanee.userIdentity.dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "loanee.userIdentity.stateOfOrigin", source = "stateOfOrigin")
    @Mapping(target = "loanee.userIdentity.maritalStatus", source = "maritalStatus")
    @Mapping(target = "loanee.userIdentity.stateOfResidence", source = "stateOfResidence")
    @Mapping(target = "loanee.userIdentity.nationality", source = "nationality")
    @Mapping(target = "loanee.userIdentity.residentialAddress", source = "residentialAddress")
    @Mapping(target = "loanee.userIdentity.phoneNumber", source = "phoneNumber")
    @Mapping(target = "loanee.userIdentity.alternateEmail", source = "alternateEmail")
    @Mapping(target = "loanee.userIdentity.alternatePhoneNumber", source = "alternatePhoneNumber")
    @Mapping(target = "loanee.userIdentity.alternateContactAddress", source = "alternateContactAddress")
    @Mapping(target = "loanee.userIdentity.nextOfKin.firstName", source = "nextOfKinFirstName")
    @Mapping(target = "loanee.userIdentity.nextOfKin.lastName", source = "nextOfKinLastName")
    @Mapping(target = "loanee.userIdentity.nextOfKin.phoneNumber", source = "nextOfKinPhoneNumber")
    @Mapping(target = "loanee.userIdentity.nextOfKin.contactAddress", source = "nextOfKinResidentialAddress")
    @Mapping(target = "loanee.loaneeLoanDetail.amountReceived", source = "amountReceived")
    @Mapping(target = "loanee.loaneeLoanDetail.amountRepaid", source = "amountPaid")
    @Mapping(target = "loanee.loaneeLoanDetail.initialDeposit", source = "initialDeposit")
    @Mapping(target = "loanee.loaneeLoanDetail.amountOutstanding", source = "amountOutstanding")
    @Mapping(target = "debtPercentage", source = "debtPercentage")
    @Mapping(target = "interestRate", source = "interestRate")
    @Mapping(target = "repaymentPercentage", source = "repaymentPercentage")
    @Mapping(target = "cohort.name", source = "cohortName")
    @Mapping(target = "cohort.id", source = "cohortId")
    @Mapping(target = "cohort.tuitionAmount", source = "tuitionAmount")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "interestIncurred", source = "interestIncurred")
    CohortLoanee mapProjectionCohortLoanee(CohortLoaneeProjection cohortLoaneeEntity);
}
