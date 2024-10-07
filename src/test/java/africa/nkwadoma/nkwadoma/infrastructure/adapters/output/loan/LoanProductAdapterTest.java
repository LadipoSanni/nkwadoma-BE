package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loan;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class LoanProductAdapterTest {
    @Autowired
    private LoanProductOutputPort loanProductOutputPort;
    private LoanProduct loanProduct;

    @BeforeEach
    void setUp() {
        loanProduct = new LoanProduct();
        loanProduct.setName("Test Loan Product");
        loanProduct.setMandate("Test: A new mandate for test");
        loanProduct.setSponsors(List.of("Mark", "Jack"));
        loanProduct.setLoanProductSize(new BigDecimal(1000));
        loanProduct.setObligorLoanLimit(new BigDecimal(1000));
        loanProduct.setInterestRate(0);
        loanProduct.setMoratorium(5);
        loanProduct.setTenor(5);
        loanProduct.setMinRepaymentAmount(new BigDecimal(1000));
        loanProduct.setTermsAndCondition("Test: A new loan for test and terms and conditions");
    }

    @Test
    void createLoanProduct() {
            LoanProduct createdLoanProduct = loanProductOutputPort.save(loanProduct);
            assertNotNull(createdLoanProduct);
            assertNotNull(createdLoanProduct.getId());
    }

}