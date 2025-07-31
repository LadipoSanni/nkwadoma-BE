package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;


import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.DailyInterestOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.DailyInterest;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class DailyInterestAdapterTest {

    private DailyInterest dailyInterest;
    private LoaneeLoanDetail loaneeLoanDetail;
    private String dailyInterestId;
    @Autowired
    private DailyInterestOutputPort dailyInterestOutputPort;
    @Autowired
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;


    @BeforeAll
    void setUp() {
        loaneeLoanDetail = TestData.createTestLoaneeLoanDetail();
        loaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
        dailyInterest = TestData.buildDailyInterest(loaneeLoanDetail);
    }


    @Order(1)
    @Test
    void saveDailyInterest() {

        DailyInterest savedDailyInterest = DailyInterest.builder().build();
        try {
            savedDailyInterest = dailyInterestOutputPort.save(dailyInterest);
            dailyInterestId = savedDailyInterest.getId();
        }catch (MeedlException meedlException) {
            log.error(meedlException.getMessage());
        }
        assertEquals(savedDailyInterest.getInterest(),BigDecimal.valueOf(5000.00));
    }


    @AfterAll
    void tearDown() throws MeedlException {
        dailyInterestOutputPort.deleteById(dailyInterestId);
        loaneeLoanDetailsOutputPort.delete(loaneeLoanDetail.getId());
    }


}
