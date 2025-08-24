package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.bankdetail;

import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.BankDetailOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class BankDetailAdapterTest {
    @Autowired
    private BankDetailOutputPort bankDetailOutputPort;
    private final BankDetail builtBankDetail = TestData.buildBankDetail();
    private String bankDetailId;

    @Test
    @Order(1)
    void saveBankDetail() {
        BankDetail savedBankDetail = null;
        try {
            savedBankDetail = bankDetailOutputPort.save(builtBankDetail);
            log.info("Saved BankDetail: {}", savedBankDetail);
            bankDetailId = savedBankDetail.getId();
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(savedBankDetail);
        assertNotNull(savedBankDetail.getId());
        assertEquals(builtBankDetail.getBankName(), savedBankDetail.getBankName());
    }
    @Test
    void saveBankDetailWithNull() {
//        assertThrows(MeedlException.class, () -> bankDetailOutputPort.save(null));
    }
    @Test
    void saveBankDetailWithNullAccountName() {
        builtBankDetail.setBankName(null);
        assertThrows(MeedlException.class, () -> bankDetailOutputPort.save(builtBankDetail));
    }
    @Test
    void saveBankDetailWithNullAccountNumber() {
        builtBankDetail.setBankNumber(null);
        assertThrows(MeedlException.class, () -> bankDetailOutputPort.save(builtBankDetail));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY, "jhifdnsc"})
    void findBankDetailWitInvalidUserId(String invalidUserId) {
        assertThrows(MeedlException.class, () -> bankDetailOutputPort.findByBankDetailId(invalidUserId));
    }
    @Test
    @Order(2)
    void findByUserId(){
        BankDetail bankDetail = null;
        try {
            bankDetail = bankDetailOutputPort.findByBankDetailId(bankDetailId);
        } catch (MeedlException e) {
            log.error("Failed to find bank detail {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        assertNotNull(bankDetail);
        assertEquals(builtBankDetail.getBankName(), bankDetail.getBankName());
        assertEquals(builtBankDetail.getBankNumber(), bankDetail.getBankNumber());
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY, "nfdjnj"})
    void deleteWithInvalidId(String invalidId) {
        assertThrows(MeedlException.class, () -> bankDetailOutputPort.deleteById(invalidId));
    }

    @Test
    @Order(3)
    void deleteById(){
        BankDetail bankDetail = null;
        try {
            bankDetail = bankDetailOutputPort.findByBankDetailId(bankDetailId);
        } catch (MeedlException e) {
            log.error("Failed to find bank detail {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        assertNotNull(bankDetail);
        try {
            bankDetailOutputPort.deleteById(bankDetailId);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertThrows(MeedlException.class, ()-> bankDetailOutputPort.findByBankDetailId(bankDetailId));
    }
}
