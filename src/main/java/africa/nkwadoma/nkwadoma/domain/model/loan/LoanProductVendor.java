package africa.nkwadoma.nkwadoma.domain.model.loan;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoanProductVendor {

    private String id;
    private Vendor vendor;
    private LoanProduct loanProduct;
}
