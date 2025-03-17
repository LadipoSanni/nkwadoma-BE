package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.bankDetail;

import africa.nkwadoma.nkwadoma.application.ports.output.bankDetail.BankDetailOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankDetail.BankDetail;
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
public class BankDetailTest {
    @Autowired
    private BankDetailOutputPort bankDetailOutputPort;
    private BankDetail bankDetail = TestData.buildBankDetail();

    @Test
    void saveBankDetail() {
        BankDetail savedBankDetail = null;
        try {
            savedBankDetail = bankDetailOutputPort.save(bankDetail);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(savedBankDetail);
        assertNotNull(savedBankDetail.getId());
        assertEquals(bankDetail.getAccountName(), savedBankDetail.getAccountName());
    }
    @Test
    void saveBankDetailWithNull() {
        assertThrows(MeedlException.class, () -> bankDetailOutputPort.save(null));
    }
    @Test
    void saveBankDetailWithNullAccountName() {
        bankDetail.setAccountName(null);
        assertThrows(MeedlException.class, () -> bankDetailOutputPort.save(bankDetail));
    }
    @Test
    void saveBankDetailWithNullAccountNumber() {
        bankDetail.setAccountNumber(null);
        assertThrows(MeedlException.class, () -> bankDetailOutputPort.save(bankDetail));
    }
}
