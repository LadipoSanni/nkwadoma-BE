package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
class LoanAdapterTest {
    @Autowired
    private LoanOutputPort loanOutputPort;
    private Loan loan;
    @BeforeEach
    public void setUp(){
        Loanee loanee = new Loanee();
        loanee.setId("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f");

    loan = new Loan();
    loan.setLoanAccountId("account id");
    loan.setLoanee(loanee);
        try {
            loan.setStartDate(LocalDateTime.now());
        } catch (MeedlException e) {
            log.error("Failed to set up loan start date : {}", e.getMessage());
        }
    }
    @Test
    void saveLoan() {
        try {
            Loan savedLoan = loanOutputPort.save(loan);
            assertNotNull(savedLoan);
            assertNotNull(savedLoan.getId());
            log.info("Saved loan: {} " , savedLoan.getId());
        } catch (MeedlException e) {
            log.error("Error saving loan {}", e.getMessage());
        }
    }
}