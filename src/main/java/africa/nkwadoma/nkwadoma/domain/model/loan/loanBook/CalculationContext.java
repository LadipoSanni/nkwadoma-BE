package africa.nkwadoma.nkwadoma.domain.model.loan.loanBook;

import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.education.ProgramLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class CalculationContext {
    private Cohort cohort;
    private Loanee loanee;
    private LoaneeLoanDetail loaneeLoanDetail;
    private CohortLoanDetail cohortLoanDetail;
    private ProgramLoanDetail programLoanDetail;
    private OrganizationLoanDetail organizationLoanDetail;
    private List<RepaymentHistory> repaymentHistories;
    private BigDecimal previousTotalAmountPaid = BigDecimal.ZERO;
    private BigDecimal previousTotalInterestIncurred = BigDecimal.ZERO;
}
