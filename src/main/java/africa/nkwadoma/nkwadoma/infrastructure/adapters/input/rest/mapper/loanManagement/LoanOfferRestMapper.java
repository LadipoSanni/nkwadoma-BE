package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement;


import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.AllLoanOfferResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.LoanOfferResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoanOfferAcceptRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.WithDrawLoanOfferResponse;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanOfferRestMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "loanOfferStatus", source = "loanOfferStatus")
    @Mapping(target = "tuitionAmount", source = "loaneeLoanDetail.tuitionAmount")
    @Mapping(target = "initialDeposit", source = "loaneeLoanDetail.initialDeposit")
    @Mapping(target = "amountRequested", source = "loaneeLoanDetail.amountRequested")
    @Mapping(target = "loaneeBreakdown", source = "loaneeBreakdown")
    @Mapping(target = "gender", source = "userIdentity.gender")
    @Mapping(target = "email", source = "userIdentity.email")
    @Mapping(target = "phoneNumber", source = "userIdentity.phoneNumber")
    @Mapping(target = "dateOfBirth", source = "userIdentity.dateOfBirth")
    @Mapping(target = "stateOfOrigin", source = "userIdentity.stateOfOrigin")
    @Mapping(target = "maritalStatus", source = "userIdentity.maritalStatus")
    @Mapping(target = "stateOfResidence", source = "userIdentity.stateOfResidence")
    @Mapping(target = "nationality", source = "userIdentity.nationality")
    @Mapping(target = "residentialAddress", source = "userIdentity.residentialAddress")
    @Mapping(target = "alternateEmail", source = "userIdentity.alternateEmail")
    @Mapping(target = "alternatePhoneNumber", source = "userIdentity.alternatePhoneNumber")
    @Mapping(target = "alternateContactAddress", source = "userIdentity.alternateContactAddress")
    @Mapping(target = "nextOfKinFirstName", source = "nextOfKin.firstName")
    @Mapping(target = "nextOfKinLastName", source = "nextOfKin.lastName")
    @Mapping(target = "nextOfKinEmail", source = "nextOfKin.email")
    @Mapping(target = "nextOfKinPhoneNumber", source = "nextOfKin.phoneNumber")
    @Mapping(target = "nextOfKinRelationship", source = "nextOfKin.nextOfKinRelationship")
    @Mapping(target = "nextOfKinContactAddress", source = "nextOfKin.contactAddress")
    @Mapping(target = "image", source = "userIdentity.image")
    @Mapping(target = "firstName", source = "userIdentity.firstName")
    @Mapping(target = "lastName", source = "userIdentity.lastName")
    @Mapping(target = "cohortName", source = "cohortName")
    @Mapping(target = "programName", source = "programName")
    @Mapping(target = "creditScore", source = "creditScore")
    @Mapping(target = "termsAndCondition", source = "termsAndCondition")
    @Mapping(target = "loaneeId", source = "loaneeId")
    @Mapping(target = "loanProductName", source = "loanProduct.name")
    LoanOfferResponse toLoanOfferResponse(LoanOffer loanOffer);

    @Mapping(target = "id", source = "loanOfferId")
    LoanOffer toLoanOffer(@Valid LoanOfferAcceptRequest loanOfferRequest);




    @Mapping(target = "id", source = "id")
    @Mapping(target = "amountRequested", source = "loaneeLoanDetail.amountRequested")
    @Mapping(target = "amountApproved", source = "loaneeLoanDetail.amountApproved")
    @Mapping(target = "firstName", source = "userIdentity.firstName")
    @Mapping(target = "lastName", source = "userIdentity.lastName")
    @Mapping(target = "dateOffered", source = "dateTimeOffered")
    @Mapping(target = "loanProductName", source = "loanProduct.name")
    @Mapping(target = "loanOfferResponse", source = "loaneeResponse")
    AllLoanOfferResponse toAllLoanOfferResponse(LoanOffer loanOffer);

    List<AllLoanOfferResponse> toLoanOfferResponses(Page<LoanOffer> loanOffers);

    @Mapping(target = "loanOfferId", source = "id")
    WithDrawLoanOfferResponse toWithDrawnLoanOfferResponse(LoanOffer loanOffer);
}