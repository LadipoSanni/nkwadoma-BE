package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loan;


import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanBreakdown;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.LoanOfferResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoanOfferAcceptRequest;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanOfferRestMapper {

    @Mapping(target = "phoneNumber", source = "loanee.userIdentity.phoneNumber")
    @Mapping(target = "dateOfBirth", source = "loanee.userIdentity.dateOfBirth")
    @Mapping(target = "stateOfOrigin", source = "loanee.userIdentity.stateOfOrigin")
    @Mapping(target = "maritalStatus", source = "loanee.userIdentity.maritalStatus")
    @Mapping(target = "stateOfResidence", source = "loanee.userIdentity.stateOfResidence")
    @Mapping(target = "nationality", source = "loanee.userIdentity.nationality")
    @Mapping(target = "residentialAddress", source = "loanee.userIdentity.residentialAddress")
    @Mapping(target = "alternatePhoneNumber", source = "loanee.userIdentity.alternatePhoneNumber")
    @Mapping(target = "alternateEmail", source = "loanee.userIdentity.alternateEmail")
    @Mapping(target = "alternateContactAddress", source = "loanRequest.nextOfKin.contactAddress")
    @Mapping(target = "gender", source = "loanee.userIdentity.gender")
    @Mapping(target = "nextOfKinEmail", source = "loanRequest.nextOfKin.email")
    @Mapping(target = "nextOfKinPhoneNumber", source = "loanRequest.nextOfKin.phoneNumber")
    @Mapping(target = "nextOfKinRelationship", source = "loanRequest.nextOfKin.nextOfKinRelationship")
    @Mapping(target = "loaneeBreakdown", source = "loanee.loanBreakdowns")
    LoanOfferResponse toLoanOfferResponse(LoanOffer loanOffer);
    @Mapping(target = "id", source = "loanOfferId")
    LoanOffer toLoanOffer(@Valid LoanOfferAcceptRequest loanOfferRequest);

    default List<LoaneeLoanBreakdown> mapBreakdowns(List<LoaneeLoanBreakdown> breakdowns) {
        if (breakdowns == null || breakdowns.isEmpty()) {
            return Collections.emptyList();
        }

        return breakdowns.stream()
                .map(this::mapBreakdownWithoutLoanee)
                .collect(Collectors.toList());
    }


    default LoaneeLoanBreakdown mapBreakdownWithoutLoanee(LoaneeLoanBreakdown breakdown) {
        if (breakdown == null) {
            return null;
        }

        LoaneeLoanBreakdown newBreakdown = new LoaneeLoanBreakdown();
        newBreakdown.setLoaneeLoanBreakdownId(breakdown.getLoaneeLoanBreakdownId());
        newBreakdown.setItemName(breakdown.getItemName());
        newBreakdown.setItemAmount(breakdown.getItemAmount());
        newBreakdown.setCurrency(breakdown.getCurrency());
        newBreakdown.setLoanee(null);
        return newBreakdown;
    }
    List<LoanOfferResponse> toLoanOfferResponses(Page<LoanOffer> loanOffers);
}
