package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanMetricsMapper {
    LoanMetricsEntity toLoanMetricsEntity(LoanMetrics loanMetrics);

    @InheritInverseConfiguration
    LoanMetrics toLoanMetrics(LoanMetricsEntity loanMetricsEntity);

    @Mapping(target = "loanRequestCount", expression = "java(existingLoanMetrics.getLoanRequestCount() + updatedLoanMetrics.getLoanRequestCount())")
    @Mapping(target = "uploadedLoanCount", expression = "java(existingLoanMetrics.getUploadedLoanCount() + updatedLoanMetrics.getUploadedLoanCount())")
    @Mapping(target = "loanReferralCount", expression = "java(existingLoanMetrics.getLoanReferralCount() + updatedLoanMetrics.getLoanReferralCount())")
    @Mapping(target = "loanOfferCount", expression = "java(existingLoanMetrics.getLoanOfferCount() + updatedLoanMetrics.getLoanOfferCount())")
    @Mapping(target = "loanDisbursalCount", expression = "java(existingLoanMetrics.getLoanDisbursalCount() + updatedLoanMetrics.getLoanDisbursalCount())")
    @Mapping(target = "organizationId", source = "existingLoanMetrics.organizationId")
    @Mapping(target = "id", source = "existingLoanMetrics.id")
    LoanMetrics updateLoanMetrics(LoanMetrics existingLoanMetrics, LoanMetrics updatedLoanMetrics);


    @Mapping(target = "firstName", source = "userIdentity.firstName")
    @Mapping(target = "lastName", source = "userIdentity.lastName")
    @Mapping(target = "deposit", source = "loaneeLoanDetail.initialDeposit")
    @Mapping(target = "offerDate", source = "dateTimeOffered")
    @Mapping(target = "amountRequested", source = "loaneeLoanDetail.amountRequested")
    @Mapping(target = "loanProductName", source = "loanProduct.name")
    LoanDetail mapLoanOfferToLoanLifeCycles(LoanOffer loanOffer);


    @Mapping(target = "cohortName", source = "cohortName")
    @Mapping(target = "programName", source = "programName")
    @Mapping(target = "amountRequested", source = "loanAmountRequested")
    @Mapping(target = "deposit", source = "initialDeposit")
    @Mapping(target = "requestedDate", source = "createdDate")
    @Mapping(target = "startDate", source = "cohortStartDate")
    LoanDetail mapLoanRequestToLoanLifeCycles(LoanRequest loanRequest);


    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "cohortName", source = "cohortName")
    @Mapping(target = "programName", source = "programName")
    @Mapping(target = "amountRequested", source = "loanAmountRequested")
    @Mapping(target = "offerDate", source = "offerDate")
    @Mapping(target = "deposit", source = "initialDeposit")
    @Mapping(target = "startDate", source = "startDate")
    LoanDetail mapToLoans(Loan loan);
}
