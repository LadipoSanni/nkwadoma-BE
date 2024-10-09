package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanProductStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.TenorStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

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
