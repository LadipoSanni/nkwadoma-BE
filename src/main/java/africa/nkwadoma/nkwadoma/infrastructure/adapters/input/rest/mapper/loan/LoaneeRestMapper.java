package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanBreakdown;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.LoaneeRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.LoanBreakdownResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.LoaneeReferralResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.LoaneeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoaneeRestMapper {

    @Mapping(target = "loanBreakdowns"  , source = "loaneeLoanDetail.loanBreakdown")
    Loanee toLoanee(LoaneeRequest loaneeRequest);


    @Mapping( target= "loaneeLoanDetail.loanBreakdown",source = "loanBreakdowns")
    @Mapping( target= "loaneeLoanDetail.initialDeposit",source = "loaneeLoanDetail.initialDeposit")
    @Mapping( target= "loaneeLoanDetail.amountRequested",source = "loaneeLoanDetail.amountRequested")
    LoaneeResponse toLoaneeResponse(Loanee loanee);

    @Mapping(target = "loanBreakdownId", source = "loaneeLoanBreakdownId")
    @Mapping(target = "itemName", source = "itemName")
    @Mapping(target = "itemAmount", source = "itemAmount")
    @Mapping(target = "currency", source = "currency")
    LoanBreakdownResponse toLoanBreakdownResponse(LoaneeLoanBreakdown loanBreakdown);

    LoaneeReferralResponse toLoaneeReferralResponse(LoanReferral loanReferral);
}
