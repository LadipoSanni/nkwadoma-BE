package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@SpringBootTest
class LoanReferralAdapterTest {
    @Autowired
    private LoanReferralOutputPort loanReferralOutputPort;
    @Autowired
    private LoaneeOutputPort loaneeOutputPort;
    private LoanReferral loanReferral;
    private Loanee loanee;
    private LoaneeLoanDetail loaneeLoanDetail;

    @BeforeEach
    void setUp() {

    }

    @Test
    void saveLoanReferral() {
//        loanReferral = new LoanReferral("LoanReferral1", "John Doe", "1234567890", "Email@example.com", "Loan Application");
        LoanReferral savedLoanReferral = loanReferralOutputPort.saveLoanReferral(loanReferral);
        assertNotNull(savedLoanReferral);
        assertNotNull(savedLoanReferral.getId());
    }
}