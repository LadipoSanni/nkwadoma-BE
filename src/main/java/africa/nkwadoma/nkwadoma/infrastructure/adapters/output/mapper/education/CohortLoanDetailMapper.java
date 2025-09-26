package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.education;

import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortLoanDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CohortLoanDetailMapper {

    CohortLoanDetailEntity toCohortLoanDetailEntity(CohortLoanDetail cohortLoanDetail);

    CohortLoanDetail toCohortLoanDetail(CohortLoanDetailEntity cohortLoanDetailEntity);
}
