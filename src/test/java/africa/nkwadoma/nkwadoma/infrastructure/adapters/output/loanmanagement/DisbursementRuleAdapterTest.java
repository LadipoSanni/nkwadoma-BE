package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.DisbursementRuleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.VendorEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.DisbursementRuleRepository;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Set;

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
        disbursementRule = TestData.buildDisbursementRule();

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
            log.info("Failed to save disbursement rule {}", exception.getMessage());
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
            log.info("Failed to find disbursement rule {}", exception.getMessage());
        }
        assertNotNull(foundDisbursementRule);
    }


    @Order(3)
    @Test
    void searchByValidNameAndStatus() throws MeedlException {
        DisbursementRule savedRule = disbursementRuleOutputPort.save(disbursementRule);

        Pageable pageable = PageRequest.of(0, 10);
        Page<DisbursementRule> results = disbursementRuleOutputPort.search(
                DisbursementRule.builder()
                        .name(disbursementRule.getName())
                        .activationStatuses(Set.of(ActivationStatus.APPROVED))
                        .pageNumber(0)
                        .pageSize(10)
                        .build()
        );

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(r -> r.getId().equals(savedRule.getId())));
    }

    @Order(4)
    @Test
    void searchByInvalidName() {
        DisbursementRule invalidSearch = DisbursementRule.builder()
                .name("")
                .activationStatuses(Set.of(ActivationStatus.APPROVED))
                .pageNumber(0)
                .pageSize(10)
                .build();

        assertThrows(MeedlException.class,
                () -> disbursementRuleOutputPort.search(invalidSearch));
    }

    @Order(5)
    @Test
    void searchByNameButDifferentStatus_ShouldReturnEmpty() throws MeedlException {
        Pageable pageable = PageRequest.of(0, 10);

        Page<DisbursementRule> results = disbursementRuleOutputPort.search(
                DisbursementRule.builder()
                        .name(disbursementRule.getName())
                        .activationStatuses(Set.of(ActivationStatus.INACTIVE)) // different status
                        .pageNumber(0)
                        .pageSize(10)
                        .build()
        );

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
    @Test
    @Order(6)
    void deleteDisbursementRule() throws MeedlException {
        DisbursementRule foundDisbursementRule = disbursementRuleOutputPort.findById(disbursementRuleId);
        assertNotNull(foundDisbursementRule);
        disbursementRuleOutputPort.deleteById(disbursementRuleId);
        assertThrows(MeedlException.class, ()->disbursementRuleOutputPort.findById(disbursementRuleId));
    }
}
