package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.loanBook.RepaymentHistoryOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.testUtilities.TestUtils;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class RepaymentHistoryAdapterTest {
    @Autowired
    private RepaymentHistoryOutputPort repaymentHistoryOutputPort;
    private RepaymentHistory repaymentHistory;
    @BeforeEach
    void setUp() {
        repaymentHistory = TestData.buildRepaymentHistory(TestUtils.generateRandomUUID());
    }
    void saveRepaymentHistory(){

    }

    @AfterEach
    void tearDown() {

        RepaymentHistory savedRepaymentHistory = repaymentHistoryOutputPort.save(repaymentHistory);
    }
}