package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement.disbursement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanProductDisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanProduct.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.disbursement.DisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.disbursement.DisbursementRule;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.domain.model.loan.disbursement.LoanProductDisbursementRule;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class LoanProductDisbursementRuleTest {
    @Autowired
    private LoanProductDisbursementRuleOutputPort loanProductDisbursementRuleOutputPort;
    @Autowired
    private DisbursementRuleOutputPort disbursementRuleOutputPort;
    private String loanProductDisbursementRuleId;
    private String loanProductId;
    private String disbursementRuleId;
    private LoanProductDisbursementRule loanProductDisbursementRule;
    private DisbursementRule disbursementRule;
    private LoanProduct loanProduct;
    @Autowired
    private LoanProductOutputPort loanProductOutputPort;


    @BeforeAll
    void setUpLoanOffer() throws MeedlException {
        disbursementRule = TestData.buildDisbursementRule();
        loanProduct = TestData.buildTestLoanProduct();
        loanProduct = loanProductOutputPort.save(loanProduct);
        disbursementRule = disbursementRuleOutputPort.save(disbursementRule);
        loanProductId = loanProduct.getId();
        disbursementRuleId = disbursementRule.getId();


        loanProductDisbursementRule = new LoanProductDisbursementRule();
        loanProductDisbursementRule.setDisbursementRule(disbursementRule);
        loanProductDisbursementRule.setLoanProduct(loanProduct);

    }


    @Test
    void saveNullDisbursementRule() {
        assertThrows(MeedlException.class, ()-> loanProductDisbursementRuleOutputPort.save(null));
    }


    @Order(1)
    @Test
    void saveDisbursementRule(){
        LoanProductDisbursementRule savedLoanProductDisbursementRule = null;
        try{
            savedLoanProductDisbursementRule = loanProductDisbursementRuleOutputPort.save(loanProductDisbursementRule);
        }catch (MeedlException exception){
            log.info("Failed to saved loan product disbursement rule {}", exception.getMessage());
        }
        assertNotNull(savedLoanProductDisbursementRule);
        assertNotNull(savedLoanProductDisbursementRule.getId());
        loanProductDisbursementRuleId = savedLoanProductDisbursementRule.getId();
        log.info("Loan product disbursement rule id in adapter test {}", savedLoanProductDisbursementRule.getId());
    }

    @Test
    void findDisbursementRuleByNullId() {
        assertThrows(MeedlException.class, () -> loanProductDisbursementRuleOutputPort.findById(null));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "invalid.id"})
    void deleteDisbursementRuleWithInvalidId(String id) {
        assertThrows(MeedlException.class, () -> loanProductDisbursementRuleOutputPort.deleteById(id));
    }


    @Order(2)
    @Test
    void findLoanProductDisbursementRuleById(){
        LoanProductDisbursementRule foundLoanProductDisbursementRule = null;
        try{
            foundLoanProductDisbursementRule = loanProductDisbursementRuleOutputPort.findById(loanProductDisbursementRuleId);
        }catch (MeedlException exception){
            log.info("Failed to find loan product disbursement rule {}", exception.getMessage());
        }
        assertNotNull(foundLoanProductDisbursementRule);
    }

    @Test
    @Order(3)
    void deleteDisbursementRule() throws MeedlException {
        LoanProductDisbursementRule foundLoanProductDisbursementRule = loanProductDisbursementRuleOutputPort.findById(loanProductDisbursementRuleId);
        assertNotNull(foundLoanProductDisbursementRule);
        loanProductDisbursementRuleOutputPort.deleteById(loanProductDisbursementRuleId);
        assertThrows(MeedlException.class, ()-> loanProductDisbursementRuleOutputPort.findById(loanProductDisbursementRuleId));
    }

    @AfterAll
    void tearDown() throws MeedlException {
        loanProductOutputPort.deleteById(loanProductId);
        disbursementRuleOutputPort.deleteById(disbursementRuleId);
    }

}
