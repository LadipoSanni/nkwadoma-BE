package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.test.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
class LoanAdapterTest {
    @Autowired
    private LoanOutputPort loanOutputPort;
    @Autowired
    private LoaneeOutputPort loaneeOutputPort;
    @Autowired
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    private final String testId = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
    private Loan loan;
    private String savedLoanId;
    private String userId;
    private String loaneeId;
    @BeforeEach
    public void setUp(){

        UserIdentity userIdentity = TestData.createTestUserIdentity("testuser@email.com");
        try {
            userIdentity = userIdentityOutputPort.save(userIdentity);
            userId = userIdentity.getId();
        } catch (MeedlException e) {
            log.error("Error saving user {}", e.getMessage());
            throw new RuntimeException(e);
        }
        LoaneeLoanDetail loaneeLoanDetail = new LoaneeLoanDetail();
        loaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
        Loanee loanee = TestData.createTestLoanee(userIdentity, loaneeLoanDetail);
        try {
            loanee = loaneeOutputPort.save(loanee);
        } catch (MeedlException e) {
            log.error("Error saving loanee {}", e.getMessage());
            throw new RuntimeException(e);
        }
        loaneeId = loanee.getId();
        loan = TestData.createTestLoan(loanee);

    }
    @Test
    void saveLoan(){
        Loan savedLoan = null;
        try {
            savedLoan = loanOutputPort.save(loan);
            savedLoanId = savedLoan.getId();
            log.info("Saved loan: {} ", savedLoan.getId());
        } catch (MeedlException e) {
            log.error("Error saving loan {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
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
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "invalid.id"})
    void findLoanById(String id){
        assertThrows(MeedlException.class,()->loanOutputPort.findLoanById(id));
    }
    @AfterEach
    void tearDown() {
        if (StringUtils.isNotEmpty(savedLoanId)) {
            loanOutputPort.deleteById(savedLoanId);
        }
        if (StringUtils.isNotEmpty(loaneeId)) {
            try {
                loaneeOutputPort.deleteLoanee(loaneeId);
            } catch (MeedlException e) {
                log.error("Error deleting loanee {}", e.getMessage());
//                throw new RuntimeException(e);
            }
        }
    }
}