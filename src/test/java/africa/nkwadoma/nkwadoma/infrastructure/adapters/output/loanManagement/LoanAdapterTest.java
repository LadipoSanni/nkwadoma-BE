package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoaneeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
class LoanAdapterTest {
    @Autowired
    private LoanOutputPort loanOutputPort;
    @Autowired
    private LoaneeRepository loaneeRepository;
    private Loan loan;
    private String savedLoanId;
    @BeforeEach
    public void setUp(){
        LoaneeEntity loaneeEntity = new LoaneeEntity();
        loaneeEntity = loaneeRepository.save(loaneeEntity);

        loan = new Loan();
        loan.setLoanAccountId("account id");
        loan.setLoanee(Loanee.builder().id(loaneeEntity.getId()).build());
        loan.setStartDate(LocalDateTime.now());

    }
    @Test
    void saveLoan() {
        Loan savedLoan = null;
        try {
            savedLoan = loanOutputPort.save(loan);
            savedLoanId = savedLoan.getId();
            log.info("Saved loan: {} ", savedLoan.getId());
        } catch (MeedlException e) {
            log.error("Error saving loan {}", e.getMessage());
        }

        assertNotNull(savedLoan);
        assertNotNull(savedLoan.getId());
    }
    @Test
    void saveLoanWithNull() {
        assertThrows(MeedlException.class, ()->loanOutputPort.save(null));
    }
    @Test
    void saveLoanWithNullLoanee() {
        loan.setLoanee(null);
        assertThrows(MeedlException.class, ()->loanOutputPort.save(loan));
    }
    @Test
    void saveLoanWithNullStartDate() {
        loan.setStartDate(null);
        assertThrows(MeedlException.class, ()->loanOutputPort.save(loan));
    }
    @AfterEach
    void tearDown() {
        loanOutputPort.deleteById(savedLoanId);
        loaneeRepository.deleteById(loan.getLoanee().getId());
    }
}