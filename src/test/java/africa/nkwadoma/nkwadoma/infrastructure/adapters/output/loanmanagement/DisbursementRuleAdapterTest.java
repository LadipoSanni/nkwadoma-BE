package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.DisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.VendorEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.DisbursementRuleRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class DisbursementRuleAdapterTest {
    @Autowired
    private DisbursementRuleOutputPort disbursementRuleOutputPort;
    private String disbursementRuleId;
    private DisbursementRule disbursementRule;


    @BeforeAll
    void setUpLoanOffer() {
        disbursementRule = new DisbursementRule();
    }


    @Test
    void saveNullDisbursementRule() {
        assertThrows(MeedlException.class, ()-> disbursementRuleOutputPort.save(null));
    }


    @Order(1)
    @Test
    void saveDisbursementRule(){
        DisbursementRule savedDisbursementRule = null;
        try{
            savedDisbursementRule = disbursementRuleOutputPort.save(disbursementRule);
        }catch (MeedlException exception){
            log.info("Failed to set up loan offer {}", exception.getMessage());
        }
        assertNotNull(savedDisbursementRule);
        assertNotNull(savedDisbursementRule.getId());
        disbursementRuleId = savedDisbursementRule.getId();
        log.info("Disbursement rule id in adapter test {}", savedDisbursementRule.getId());
    }

    @Test
    void findDisbursementRuleByNullId() {
        assertThrows(MeedlException.class, () -> disbursementRuleOutputPort.findById(null));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "invalid.id"})
    void deleteDisbursementRuleWithInvalidId(String id) {
        assertThrows(MeedlException.class, () -> disbursementRuleOutputPort.deleteById(id));
    }


    @Order(2)
    @Test
    void findDisbursementRuleById(){
        DisbursementRule foundDisbursementRule = null;
        try{
            foundDisbursementRule = disbursementRuleOutputPort.findById(disbursementRuleId);
        }catch (MeedlException exception){
            log.info("Failed to find loan Offer {}", exception.getMessage());
        }
        assertNotNull(foundDisbursementRule);
    }

    @Test
    @Order(3)
    void deleteDisbursementRule() throws MeedlException {
        DisbursementRule foundDisbursementRule = disbursementRuleOutputPort.findById(disbursementRuleId);
        assertNotNull(foundDisbursementRule);
        disbursementRuleOutputPort.deleteById(disbursementRuleId);
        assertThrows(MeedlException.class, ()->disbursementRuleOutputPort.findById(disbursementRuleId));
    }

}
