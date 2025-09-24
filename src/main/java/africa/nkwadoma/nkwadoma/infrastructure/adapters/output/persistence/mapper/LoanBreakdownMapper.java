package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanBreakdownEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanBreakdownMapper {
    List<LoanBreakdown> toLoanBreakdownList(List<LoanBreakdownEntity> loanBreakdownEntities);

    List<LoanBreakdownEntity> toLoanBreakdownEntityList(List<LoanBreakdown> loanBreakdown);

    LoanBreakdown toLoanBreakDown(LoanBreakdownEntity loanBreakdownEntity);
}
