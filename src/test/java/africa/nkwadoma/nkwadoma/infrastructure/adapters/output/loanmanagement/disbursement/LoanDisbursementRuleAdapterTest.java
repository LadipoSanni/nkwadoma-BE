package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement.disbursement;


import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.disbursement.DisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.disbursement.LoanDisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.model.loan.disbursement.DisbursementRule;
import africa.nkwadoma.nkwadoma.domain.model.loan.disbursement.LoanDisbursementRule;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.disbursement.LoanDisbursementRuleRepository;
import africa.nkwadoma.nkwadoma.testUtilities.TestUtils;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class LoanDisbursementRuleAdapterTest {

    @Autowired
    private LoanDisbursementRuleOutputPort loanDisbursementRuleOutputPort;

    @Autowired
    private LoanDisbursementRuleRepository loanDisbursementRuleRepository;

    private Loan loan;
    private LoanDisbursementRule loanDisbursementRule;
    private DisbursementRule disbursementRule;
    @Autowired
    private LoanOutputPort loanOutputPort;
    @Autowired
    private DisbursementRuleOutputPort disbursementRuleOutputPort;
    private String loanId;
    private String disbursementRuleId;
    private String loanDisbursementRuleId;

    @BeforeAll
    void setup() throws MeedlException {
        loanDisbursementRuleRepository.deleteAll();

        loan = TestData.createTestLoan(
                TestData.createTestLoanee(
                        TestData.createTestUserIdentity(
                                TestUtils.generateEmail(8)), TestData.createTestLoaneeLoanDetail()));

        loan.setLoanStatus(LoanStatus.PERFORMING);
        loan = loanOutputPort.save(loan);
        loanId = loan.getId();

        disbursementRule = TestData.buildDisbursementRule();
        disbursementRule = disbursementRuleOutputPort.save(disbursementRule);
        disbursementRuleId = disbursementRule.getId();
         loanDisbursementRule = LoanDisbursementRule
                .builder()
                .disbursementRule(disbursementRule)
                .loan(loan)
                .build();
    }

    @AfterAll
    void teardown() throws MeedlException {
        disbursementRuleOutputPort.deleteById(disbursementRuleId);
        loanOutputPort.deleteById(loanId);
    }

    @Test
    @Order(1)
    void save() throws MeedlException {


        LoanDisbursementRule savedLoandisbursementRule = loanDisbursementRuleOutputPort.save(loanDisbursementRule);

        assertNotNull(savedLoandisbursementRule.getId());

        loanDisbursementRuleId = savedLoandisbursementRule.getId();
    }

    @Test
    void saveWithNullLoanDisbursementRule() {
        assertThrows(MeedlException.class, () -> loanDisbursementRuleOutputPort.save(null));
    }

    @Test
    @Order(2)
    void findById() throws MeedlException {
        loanDisbursementRule.setId(loanDisbursementRuleId);
        LoanDisbursementRule foundLoanDisbursementRule = loanDisbursementRuleOutputPort.findById(loanDisbursementRule);

        assertEquals(loanDisbursementRuleId, foundLoanDisbursementRule.getId());
    }

    @Test
    void findByIdWithInvalidUuid() {
        LoanDisbursementRule probe = LoanDisbursementRule.builder()
                .id("bad-uuid")
                .build();

        assertThrows(MeedlException.class, () -> loanDisbursementRuleOutputPort.findById(probe));
    }

    @Test
    void findNonExistingLoanDisbursementRuleById() {
        LoanDisbursementRule probe = LoanDisbursementRule
                .builder()
                .id(UUID.randomUUID().toString())
                .build();

        assertThrows(MeedlException.class, () -> loanDisbursementRuleOutputPort.findById(probe));
    }

    @Test
    @Order(3)
    void findAllByLoanIdAndDisbursementRuleId() throws MeedlException {

        List<LoanDisbursementRule> results = loanDisbursementRuleOutputPort
                .findAllByLoanIdAndDisbursementRuleId(loanId, disbursementRuleId);

    }

    @Test
    void findAllByInvalidLoanIdAndDisbursementRuleId() {
        assertThrows(MeedlException.class,
                () -> loanDisbursementRuleOutputPort.findAllByLoanIdAndDisbursementRuleId("bad-id", UUID.randomUUID().toString()));
    }

    @Test
    void findAllByLoanIdAndInvalidDisbursementRuleId() {
        assertThrows(MeedlException.class,
                () -> loanDisbursementRuleOutputPort.findAllByLoanIdAndDisbursementRuleId(UUID.randomUUID().toString(), "bad-id"));
    }

    @Test
    void findAllByValidButNonExistingLoanIdAndDisbursementRuleId() throws MeedlException {
        List<LoanDisbursementRule> results = loanDisbursementRuleOutputPort
                .findAllByLoanIdAndDisbursementRuleId(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        assertTrue(results.isEmpty());
    }
    @Test
    void deleteLoanDisbursementRuleWithInvalidId(){
        assertThrows(MeedlException.class,
                () -> loanDisbursementRuleOutputPort.deleteById("bad-id"));
    }
    @Test
    void deleteLoanDisbursementRuleById() throws MeedlException {
        LoanDisbursementRule foundLoanDisbursementRule = loanDisbursementRuleOutputPort.findById(loanDisbursementRule);
        assertEquals(loanDisbursementRuleId, foundLoanDisbursementRule.getId());
        loanDisbursementRuleOutputPort.deleteById(loanDisbursementRuleId);
        assertThrows(MeedlException.class, () -> loanDisbursementRuleOutputPort.findById(loanDisbursementRule));

    }

}
