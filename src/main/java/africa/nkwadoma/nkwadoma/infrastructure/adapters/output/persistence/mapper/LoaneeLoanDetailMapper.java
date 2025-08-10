package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoanDetailSummary;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeLoanDetailEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanSummaryProjection;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoaneeLoanDetailMapper {
    LoaneeLoanDetail toLoaneeLoanDetails(LoaneeLoanDetailEntity loaneeLoanDetailEntity);

    LoaneeLoanDetailEntity toLoaneeLoanDetailsEnitity(LoaneeLoanDetail loaneeLoanDetail);

    LoanDetailSummary mapLoanSummaryProjectionToLOanSummary(LoanSummaryProjection loanSummaryProjection);
}
