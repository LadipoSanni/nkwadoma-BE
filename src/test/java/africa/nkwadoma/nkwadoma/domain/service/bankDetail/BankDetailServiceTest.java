package africa.nkwadoma.nkwadoma.domain.service.bankDetail;

import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.BankDetailOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.BankDetailMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static africa.nkwadoma.nkwadoma.domain.enums.IdentityRole.MEEDL_ADMIN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class BankDetailServiceTest {

    @InjectMocks
    private BankDetailService bankDetailService;

    @Mock
    private BankDetailOutputPort bankDetailOutputPort;

    private BankDetail builtBankDetail;

    @BeforeEach
    void setUp() {
        builtBankDetail = BankDetail.builder()
                .bankName("Lagos Main")
                .bankNumber("1234567890")
                .build();
    }

    @Test
    void addBankDetailsWithValidData() throws MeedlException {
        BankDetail savedDetail = BankDetail.builder()
                .id("123")
                .bankName(builtBankDetail.getBankName())
                .bankNumber(builtBankDetail.getBankNumber())
                .build();

        when(bankDetailOutputPort.save(any(BankDetail.class))).thenReturn(savedDetail);

        BankDetail result = bankDetailService.addBankDetails(builtBankDetail);

        assertNotNull(result);
        assertEquals("123", result.getId());
        assertEquals("Added bank details successfully", result.getResponse());
        verify(bankDetailOutputPort, times(1)).save(any(BankDetail.class));
    }

    @Test
    void addBankDetailsWithNullBankDetail() {
        MeedlException exception = assertThrows(MeedlException.class,
                () -> bankDetailService.addBankDetails(null));

        assertTrue(exception.getMessage().contains(BankDetailMessages.INVALID_BANK_DETAIL.getMessage()));
        verifyNoInteractions(bankDetailOutputPort);
    }

    @Test
    void addBankDetailsWithInvalidBankDetail() throws MeedlException {
        BankDetail invalidDetail = spy(builtBankDetail);
        doThrow(new MeedlException("Validation failed")).when(invalidDetail).validate();

        MeedlException exception = assertThrows(MeedlException.class,
                () -> bankDetailService.addBankDetails(invalidDetail));

        assertEquals("Validation failed", exception.getMessage());
        verifyNoInteractions(bankDetailOutputPort);
    }
}
