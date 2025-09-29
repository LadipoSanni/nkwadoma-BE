package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanee;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoanDetailSummary;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanAggregate;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeLoanAggregateEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanSummaryProjection;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeLoanAggregateProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoaneeLoanAggregateMapper {
    
    LoaneeLoanAggregateEntity toLoaneeLoanAggregateEntity(LoaneeLoanAggregate loaneeLoanAggregate);

    LoaneeLoanAggregate toLoaneeLoanAggregate(LoaneeLoanAggregateEntity loaneeLoanAggregateEntity);

    LoaneeLoanAggregate mapProjectionToLoaneeLoanAggregate(LoaneeLoanAggregateProjection loaneeLoanAggregateProjection);

    LoanDetailSummary mapLoanSummaryProjectionToLoanDetailSummary(LoanSummaryProjection loanSummaryProjection);

    @Mapping(target = "totalAmountReceived", source = "historicalDebt")
    @Mapping(target = "totalAmountOutstanding", source = "totalAmountOutstanding")
//    @Mapping(target = "totalAmountRepaid", source = "totalAmountRepaid")
    LoanDetailSummary mapLoaneeLoanAggregateTOLoanDetailSummary(LoaneeLoanAggregate loaneeLoanAggregate);
}
