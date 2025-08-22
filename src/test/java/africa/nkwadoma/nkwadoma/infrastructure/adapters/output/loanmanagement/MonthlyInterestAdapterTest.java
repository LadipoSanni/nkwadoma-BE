package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.MonthlyInterestOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.MonthlyInterest;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MonthlyInterestAdapterTest {


    private MonthlyInterest monthlyInterest;
    private LoaneeLoanDetail loaneeLoanDetail;
    private String monthlyInterestId;
    @Autowired
    private MonthlyInterestOutputPort monthlyInterestOutputPort;
    @Autowired
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;


    @BeforeAll
    void setUp() {
        loaneeLoanDetail = TestData.createTestLoaneeLoanDetail();
        loaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
        monthlyInterest = TestData.buildMonthlyInterest(loaneeLoanDetail);
    }


    @Test
    void cannotSaveNullDailyInterest() {
        assertThrows(MeedlException.class, () -> monthlyInterestOutputPort.save(null));
    }

    @Test
    void cannotSaveDailyInterestWithNullLoaneeLoanDetail() {
        monthlyInterest.setLoaneeLoanDetail(null);
        assertThrows(MeedlException.class, () -> monthlyInterestOutputPort.save(monthlyInterest));
    }

    @Test
    void cannotSaveDailyInterestWithNullInterest() {
        monthlyInterest.setInterest(null);
        assertThrows(MeedlException.class, () -> monthlyInterestOutputPort.save(monthlyInterest));
    }

    @Test
    void cannotSaveDailyInterestWithNullCreatedAt() {
        monthlyInterest.setCreatedAt(null);
        assertThrows(MeedlException.class, () -> monthlyInterestOutputPort.save(monthlyInterest));
    }


    @Order(1)
    @Test
    void saveDailyInterest() {

        MonthlyInterest savedMonthlyInterest = MonthlyInterest.builder().build();
        try {
            savedMonthlyInterest = monthlyInterestOutputPort.save(monthlyInterest);
            monthlyInterestId = savedMonthlyInterest.getId();
        }catch (MeedlException meedlException) {
            log.error(meedlException.getMessage());
        }
        assertEquals(savedMonthlyInterest.getInterest(), BigDecimal.valueOf(5000.00));
    }

    @Order(2)
    @Test
    void findMonthlyInterestByCreatedDate() {
        MonthlyInterest foundMonthlyInterest = MonthlyInterest.builder().build();
        try{
            foundMonthlyInterest = monthlyInterestOutputPort.findByDateCreated(monthlyInterest.getCreatedAt(),loaneeLoanDetail.getId());
        }catch (MeedlException meedlException) {
            log.error(meedlException.getMessage());
        }
        assertEquals(foundMonthlyInterest.getId(), monthlyInterestId);
    }

    @AfterAll
    void tearDown() throws MeedlException {
        monthlyInterestOutputPort.deleteById(monthlyInterestId);
        loaneeLoanDetailsOutputPort.delete(loaneeLoanDetail.getId());
    }

}
