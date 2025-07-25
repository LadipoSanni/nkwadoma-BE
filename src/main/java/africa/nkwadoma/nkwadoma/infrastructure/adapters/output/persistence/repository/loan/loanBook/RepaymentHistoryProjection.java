package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.loanBook;

import africa.nkwadoma.nkwadoma.domain.enums.loanee.ModeOfPayment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface RepaymentHistoryProjection {


    String getId();
    String getFirstName();
    String getLastName();
    LocalDateTime getPaymentDateTime();
    String getCohortId();
    BigDecimal getAmountPaid();
    BigDecimal getTotalAmountRepaid();
    BigDecimal getAmountOutstanding();
    ModeOfPayment getModeOfPayment();
    Integer getFirstYear();
    Integer getLastYear();
    BigDecimal getInterestIncurred();
}
