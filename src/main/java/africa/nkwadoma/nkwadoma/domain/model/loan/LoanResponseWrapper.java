package africa.nkwadoma.nkwadoma.domain.model.loan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseWrapper {
    private Page<Loan> loans;
    private LoanDetailSummary loanDetailSummary;
}
