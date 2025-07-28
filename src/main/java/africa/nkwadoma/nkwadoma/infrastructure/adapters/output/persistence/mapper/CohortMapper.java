package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanBreakdownEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "numberOfLoanees", source = "numberOfLoanees")
    @Mapping(target = "tuitionAmount", source = "tuitionAmount")
    @Mapping(target = "totalAmountReceived", source = "amountReceived")
    @Mapping(target = "totalAmountRequested", source = "amountRequested")
    @Mapping(target = "totalOutstandingAmount", source = "amountOutstanding")
    @Mapping(target = "totalAmountRepaid", source = "amountRepaid")
    Cohort mapFromProjectionToCohort(CohortProjection cohortProjection);

    @Mapping(target = "totalAmountRequested", source = "amountRequested")
    @Mapping(target = "totalOutstandingAmount", source = "outstandingAmount")
    @Mapping(target = "totalAmountReceived", source = "amountReceived")
    @Mapping(target = "totalAmountRepaid", source = "amountRepaid")
    @Mapping(target = "id", ignore = true)
    void mapCohortLoanDetailToCohort(@MappingTarget Cohort cohort,CohortLoanDetail cohortLoanDetail);
}
