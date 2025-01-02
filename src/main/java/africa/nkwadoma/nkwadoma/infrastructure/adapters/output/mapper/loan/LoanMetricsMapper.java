package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanMetricsMapper {
    LoanMetricsEntity toLoanMetricsEntity(LoanMetrics loanMetrics);

    @InheritInverseConfiguration
    LoanMetrics toLoanMetrics(LoanMetricsEntity loanMetricsEntity);

    @Mapping(target = "loanRequestCount", expression = "java(existingLoanMetrics.getLoanRequestCount() + updatedLoanMetrics.getLoanRequestCount())")
    @Mapping(target = "loanReferralCount", expression = "java(existingLoanMetrics.getLoanReferralCount() + updatedLoanMetrics.getLoanReferralCount())")
    @Mapping(target = "loanOfferCount", expression = "java(existingLoanMetrics.getLoanOfferCount() + updatedLoanMetrics.getLoanOfferCount())")
    @Mapping(target = "loanDisbursalCount", expression = "java(existingLoanMetrics.getLoanDisbursalCount() + updatedLoanMetrics.getLoanDisbursalCount())")
    @Mapping(target = "organizationId", source = "existingLoanMetrics.organizationId")
    @Mapping(target = "id", source = "existingLoanMetrics.id")
    LoanMetrics updateLoanMetrics(LoanMetrics existingLoanMetrics, LoanMetrics updatedLoanMetrics);

}
