package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.TenorStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages.INVALID_LOAN_PRODUCT_REQUEST_DETAILS;

@Getter
@Setter
@ToString
public class LoanProduct {
    private String id;
    private String name;
    private int moratorium;
    private ActivationStatus loanProductStatus;
    private TenorStatus tenorStatus;
    private int tenor;
    private double interestRate;
    @Size(max=2500)
    private String termsAndCondition;
    private BigDecimal obligorLoanLimit;
    private BigDecimal loanProductSize;
    private BigDecimal amountAvailable;
    private LocalDateTime createdAtDate;
    private LocalDateTime updatedAtDate;
    private BigDecimal amountEarned ;
    private BigDecimal amountDisbursed;
    private BigDecimal amountRepaid ;

    @Size(max=2500)
    private String mandate;

    private List<String> sponsors;
    private BigDecimal minRepaymentAmount;
    private String bankPartner;
    private String disbursementTerms;
    private String fundProductId;
    private int numberOfLoanees;

//    private static void validateLoanProductDetails(LoanProduct loanProduct) throws MeedlException {
//        //MeedlValidator.
//        validateDataElement(loanProduct.getTermsAndCondition());
//        validateTenor(loanProduct);
//        validateMoratorium(loanProduct);
//        if (loanProduct.getSponsors() == null
//                ||loanProduct.getSponsors().isEmpty()
//                ||loanProduct.getLoanProductSize() == null
//                ||loanProduct.getLoanProductSize().compareTo(BigDecimal.ZERO) <= ZERO
//                ||loanProduct.getObligorLoanLimit() == null
//                ||loanProduct.getObligorLoanLimit().compareTo(BigDecimal.ZERO) < ZERO
//                ||loanProduct.getInterestRate() < ZERO
//                ||loanProduct.getMinRepaymentAmount() == null
//                ||loanProduct.getMinRepaymentAmount().compareTo(BigDecimal.ZERO) < ZERO
//        ) {
//            throwException(INVALID_LOAN_PRODUCT_REQUEST_DETAILS);
//        }
//        validateObligorAgainstProductSize(loanProduct);
//    }

}
