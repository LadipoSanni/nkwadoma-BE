package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;


import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanBreakdown;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeLoanBreakdownEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

public interface LoaneeLoanBreakDownMapper {
    List<LoaneeLoanBreakdownEntity> toLoaneeLoanBreakdownEntities(List<LoaneeLoanBreakdown> loaneeLoanBreakdowns);

    List<LoaneeLoanBreakdown> toLoaneeLoanBreakdown(List<LoaneeLoanBreakdownEntity> loanBreakdownEntities);

    LoaneeLoanBreakdown toLoanBreakdown(LoaneeLoanBreakdownEntity loaneeLoanBreakdown);
}
