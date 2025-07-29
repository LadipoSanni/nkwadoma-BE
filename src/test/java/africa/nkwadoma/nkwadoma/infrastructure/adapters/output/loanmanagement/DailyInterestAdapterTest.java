package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;


import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.DailyInterestOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.DailyInterest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class DailyInterestAdapterTest {

    private DailyInterest dailyInterest;
    @Autowired
    private DailyInterestOutputPort dailyInterestOutputPort;




    @Order(1)
    @Test
    void saveDailyInterest() {

        DailyInterest savedDailyInterest = new DailyInterest();
        try {
            savedDailyInterest = dailyInterestOutputPort.save(dailyInterest);
        }catch (MeedlException meedlException) {
            log.error(meedlException.getMessage());
        }
        assertEquals("",savedDailyInterest.getInterest(),BigDecimal.valueOf(2000));

    }


}
