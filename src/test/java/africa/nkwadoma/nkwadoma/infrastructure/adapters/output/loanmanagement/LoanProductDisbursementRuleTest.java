package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanProductDisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProductDisbursementRule;
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
    private String disbursementRuleId;
    private LoanProductDisbursementRule loanProductDisbursementRule;


    @BeforeAll
    void setUpLoanOffer() {
        loanProductDisbursementRule = new LoanProductDisbursementRule();
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
            log.info("Failed to set up loan offer {}", exception.getMessage());
        }
        assertNotNull(savedLoanProductDisbursementRule);
        assertNotNull(savedLoanProductDisbursementRule.getId());
        disbursementRuleId = savedLoanProductDisbursementRule.getId();
        log.info("Disbursement rule id in adapter test {}", savedLoanProductDisbursementRule.getId());
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
            foundLoanProductDisbursementRule = loanProductDisbursementRuleOutputPort.findById(disbursementRuleId);
        }catch (MeedlException exception){
            log.info("Failed to find loan Offer {}", exception.getMessage());
        }
        assertNotNull(foundLoanProductDisbursementRule);
    }

    @Test
    @Order(3)
    void deleteDisbursementRule() throws MeedlException {
        LoanProductDisbursementRule foundLoanProductDisbursementRule = loanProductDisbursementRuleOutputPort.findById(disbursementRuleId);
        assertNotNull(foundLoanProductDisbursementRule);
        loanProductDisbursementRuleOutputPort.deleteById(disbursementRuleId);
        assertThrows(MeedlException.class, ()-> loanProductDisbursementRuleOutputPort.findById(disbursementRuleId));
    }

}
