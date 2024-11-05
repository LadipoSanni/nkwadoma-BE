package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanBreakdownEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CohortMapper {


    CohortEntity toCohortEntity(Cohort cohort);

    Cohort toCohort(CohortEntity cohortEntity);

    Cohort cohortToUpdateCohort(Cohort cohort);

    LoanBreakdownEntity mapToLoanBreakdownEntity(LoanBreakdown loanBreakdown);

    LoanBreakdown mapToLoanBreakdown(LoanBreakdownEntity loanBreakdownEntity);
}
