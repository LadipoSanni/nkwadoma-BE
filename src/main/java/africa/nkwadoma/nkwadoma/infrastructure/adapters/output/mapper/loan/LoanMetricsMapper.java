package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanMetricsMapper {
    LoanMetricsEntity toLoanMetricsEntity(LoanMetrics loanMetrics);
    @InheritInverseConfiguration
    LoanMetrics toLoanMetrics(LoanMetricsEntity loanMetricsEntity);
}
