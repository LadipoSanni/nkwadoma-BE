package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;


import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortLoanDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CohortLoanDetailsMapper {


    CohortLoanDetail toCohortLoanDetails(CohortLoanDetailEntity cohortLoanDetailEntity);

    CohortLoanDetailEntity toCohortLoanDetailsEntity(CohortLoanDetail cohortLoanDetail);
}
