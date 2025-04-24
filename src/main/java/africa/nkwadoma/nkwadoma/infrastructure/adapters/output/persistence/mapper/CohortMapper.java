package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanBreakdownEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CohortMapper {


    CohortEntity toCohortEntity(Cohort cohort);

    Cohort toCohort(CohortEntity cohortEntity);

    LoanBreakdownEntity mapToLoanBreakdownEntity(LoanBreakdown loanBreakdown);

    LoanBreakdown mapToLoanBreakdown(LoanBreakdownEntity loanBreakdownEntity);

    List<Cohort> toCohortList(List<CohortEntity> cohortEntities);
    Cohort mapFromCohortToCohort(Cohort cohort);

    void updateCohort(@MappingTarget Cohort foundCohort, Cohort cohort);
}
