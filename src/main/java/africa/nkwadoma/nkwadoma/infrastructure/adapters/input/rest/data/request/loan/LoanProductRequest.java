package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanProductStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.TenorStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class LoanProductRequest {
    private String fundProductId;
    private TenorStatus tenorStatus;
    private BigDecimal amountAvailable;
    private BigDecimal amountEarned ;
    private BigDecimal amountDisbursed;
    private BigDecimal amountRepaid;
    private LoanProductStatus loanProductStatus;
    private String bankPartner;
    private String disbursementTerms;


}
