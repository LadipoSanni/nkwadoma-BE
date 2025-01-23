package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanRestMapper {
    Loan toLoan(LoanQueryRequest loanQueryRequest);
//    @Mapping(target = "loaneeLoanBreakDownResponse", source = "loaneeLoanBreakdowns")
    LoanQueryResponse toLoanQueryResponse(Loan loan);

    default LoaneeLoanBreakDownResponse toLoaneeLoanBreakDownResponse(LoaneeLoanBreakdown loaneeLoanBreakDown) {
        return LoaneeLoanBreakDownResponse.builder().
                loaneeLoanBreakdownId(loaneeLoanBreakDown.getLoaneeLoanBreakdownId()).
                itemAmount(loaneeLoanBreakDown.getItemAmount()).
                itemName(loaneeLoanBreakDown.getItemName()).
                build();
    }

}
