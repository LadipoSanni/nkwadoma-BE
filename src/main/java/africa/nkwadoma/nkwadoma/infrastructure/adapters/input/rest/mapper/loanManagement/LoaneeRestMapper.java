package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement;

import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.DeferProgramRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoaneeLoanBreakdownRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoaneeRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.CohortLoaneeResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.LoanBeneficiaryResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.LoanBreakdownResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanee.LoaneeLoanDetailResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanee.LoaneeReferralResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanee.LoaneeResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoaneeRestMapper {

    @Mapping(target = "loanBreakdowns"  , source = "loaneeLoanDetail.loanBreakdown")
    Loanee toLoanee(LoaneeRequest loaneeRequest);

    @Mapping(target= "cohortId", source = "cohortId")
    @Mapping(target= "programId", source = "programId")
    @Mapping(target = "deferReason", source = "deferReason")
    Loanee toLoanee(DeferProgramRequest deferProgramRequest);

    @Mapping( target= "loaneeLoanDetail.loanBreakdown",source = "loanBreakdowns")
    @Mapping( target= "loaneeLoanDetail.initialDeposit",source = "loaneeLoanDetail.initialDeposit")
    @Mapping( target= "loaneeLoanDetail.amountRequested",source = "loaneeLoanDetail.amountRequested")
    @Mapping(target = "creditScore", source = "creditScore")
    @Mapping(target = "loaneeLoanDetail.id", source = "loaneeLoanDetail.id")
    @Mapping(target = "loaneeLoanDetail.amountReceived", source= "loaneeLoanDetail.amountReceived")
    @Mapping(target = "loaneeLoanDetail.amountRepaid", source= "loaneeLoanDetail.amountRepaid")
    @Mapping(target = "loaneeLoanDetail.amountOutstanding", source= "loaneeLoanDetail.amountOutstanding")
    @Mapping(target = "loaneeLoanDetail.tuitionAmount", source = "loaneeLoanDetail.tuitionAmount")
    @Mapping(target = "cohortStartDate", source = "cohortStartDate")
    @Mapping(target = "loanId", source= "loanId")
    @Mapping(target = "interestRate", source = "interestRate")
    @Mapping(target = "paymentMoratoriumPeriod", source = "paymentMoratoriumPeriod")
    @Mapping(target = "tenor", source = "tenor")
    @Mapping(target = "termsAndConditions", source = "termsAndConditions")
    @Mapping(target = "institutionName", source = "institutionName")
    @Mapping(target = "uploadedStatus", source = "uploadedStatus")
    @Mapping(target = "onboardingMode", source = "onboardingMode")
    LoaneeResponse toLoaneeResponse(Loanee loanee);
    List<LoaneeResponse> toLoaneeResponse(List<Loanee> loanees);


    @Mapping(target = "loanBreakdownId", source = "loaneeLoanBreakdownId")
    @Mapping(target = "itemName", source = "itemName")
    @Mapping(target = "itemAmount", source = "itemAmount")
    @Mapping(target = "currency", source = "currency")
    LoanBreakdownResponse toLoanBreakdownResponse(LoaneeLoanBreakdown loanBreakdown);

    @Mapping(target = "loaneeLoanBreakdownId", source = "loanBreakdownId")
    LoaneeLoanBreakdown mapToLoanBreakdown(LoaneeLoanBreakdownRequest breakdownRequest);

    LoaneeReferralResponse toLoaneeReferralResponse(LoanReferral loanReferral);

    List<LoaneeResponse> toLoaneeResponses(List<Loanee> loanee);

    @Mapping(target = "firstName", source = "loanee.userIdentity.firstName")
    @Mapping(target = "lastName", source = "loanee.userIdentity.lastName")
    @Mapping(target = "instituteName", source = "referredBy")
    LoanBeneficiaryResponse toLoanBeneficiaryResponse(CohortLoanee loanee);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userIdentity.createdBy",  expression = "java(createdBy)")
    Loanee map(String id, @Context String createdBy);

    List<Loanee> map(List<String> ids, @Context String createdBy);

    
    
    @Mapping(target = "userIdentity", source = "loanee.userIdentity")
    @Mapping(target = "cohortId", source = "cohort.id")
    @Mapping(target = "id", source = "loanee.id")
    @Mapping(target = "activationStatus", source = "loanee.activationStatus")
    LoaneeResponse mapToLoaneeResponse(CohortLoanee cohortLoanee);

    @Mapping(target = "firstName", source = "loanee.userIdentity.firstName")
    @Mapping(target = "lastName", source = "loanee.userIdentity.lastName")
    @Mapping(target = "gender", source = "loanee.userIdentity.gender")
    @Mapping(target = "dateOfBirth", source = "loanee.userIdentity.dateOfBirth")
    @Mapping(target = "stateOfOrigin", source = "loanee.userIdentity.stateOfOrigin")
    @Mapping(target = "maritalStatus", source = "loanee.userIdentity.maritalStatus")
    @Mapping(target = "stateOfResidence", source = "loanee.userIdentity.stateOfResidence")
    @Mapping(target = "nationality", source = "loanee.userIdentity.nationality")
    @Mapping(target = "residentialAddress", source = "loanee.userIdentity.residentialAddress")
    @Mapping(target = "phoneNumber", source = "loanee.userIdentity.phoneNumber")
    @Mapping(target = "alternateEmail", source = "loanee.userIdentity.alternateEmail")
    @Mapping(target = "alternatePhoneNumber", source = "loanee.userIdentity.alternatePhoneNumber")
    @Mapping(target = "alternateContactAddress", source = "loanee.userIdentity.alternateContactAddress")
    @Mapping(target = "nextOfKinFirstName", source = "loanee.userIdentity.nextOfKin.firstName")
    @Mapping(target = "nextOfKinLastName", source = "loanee.userIdentity.nextOfKin.lastName")
    @Mapping(target = "nextOfKinPhoneNumber", source = "loanee.userIdentity.nextOfKin.phoneNumber")
    @Mapping(target = "nextOfKinResidentialAddress", source = "loanee.userIdentity.nextOfKin.contactAddress")
    @Mapping(target = "amountReceived", source = "loanee.loaneeLoanDetail.amountReceived")
    @Mapping(target = "amountRepaid", source = "loanee.loaneeLoanDetail.amountRepaid")
    @Mapping(target = "amountOutstanding", source = "loanee.loaneeLoanDetail.amountOutstanding")
    @Mapping(target = "debtPercentage", source = "debtPercentage")
    @Mapping(target = "interestRate", source = "interestRate")
    @Mapping(target = "repaymentPercentage", source = "repaymentPercentage")
    @Mapping(target = "cohortName", source = "cohort.name")
    @Mapping(target = "interestIncurred", source = "interestIncurred")
    CohortLoaneeResponse toCohortLoaneeResponse(CohortLoanee cohortLoanee);

    LoaneeLoanDetailResponse toLoaneeLoanDetail(LoaneeLoanDetail loaneeLoanDetail);
}
