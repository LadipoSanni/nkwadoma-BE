package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoaneeLoanDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoaneeLoanDetailMapper {
    LoaneeLoanDetail toLoaneeLoanDetails(LoaneeLoanDetailEntity loaneeLoanDetailEntity);

    LoaneeLoanDetailEntity toLoaneeLoanDetailsEnitity(LoaneeLoanDetail loaneeLoanDetail);
}
