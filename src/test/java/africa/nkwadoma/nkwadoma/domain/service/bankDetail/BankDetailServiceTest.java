package africa.nkwadoma.nkwadoma.domain.service.bankDetail;

import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.BankDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.FinancierBankDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.BankDetailMessages;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.FinancierBankDetail;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

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
    @Mock
    private FinancierBankDetailOutputPort financierBankDetailOutputPort;
    @Mock
    private FinancierOutputPort financierOutputPort;
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;

    private BankDetail builtBankDetail;

    @BeforeEach
    void setUp() {
        builtBankDetail = BankDetail.builder()
                .userId(UUID.randomUUID().toString())
                .bankName("Lagos Main")
                .bankNumber("1234567890")
                .build();
    }

//    @Test
//    void addBankDetailsWithValidData() throws MeedlException {
//        BankDetail savedDetail = BankDetail.builder()
//                .id("123")
//                .bankName(builtBankDetail.getBankName())
//                .bankNumber(builtBankDetail.getBankNumber())
//                .build();
//
//        when(bankDetailOutputPort.save(any(BankDetail.class))).thenReturn(savedDetail);
//
//        BankDetail result = bankDetailService.addBankDetails(builtBankDetail);
//
//        assertNotNull(result);
//        assertEquals("123", result.getId());
//        assertEquals("Added bank details successfully", result.getResponse());
//        verify(bankDetailOutputPort, times(1)).save(any(BankDetail.class));
//    }

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



        @Test
        void viewBankDetail_WithNullBankDetail_ThrowsException() {
            MeedlException ex = assertThrows(MeedlException.class,
                    () -> bankDetailService.viewBankDetail(null));

            assertTrue(ex.getMessage().contains("Bank detail request cannot be empty."));
            verifyNoInteractions(userIdentityOutputPort, bankDetailOutputPort);
        }

        @Test
        void viewBankDetail_WithInvalidUUID_ThrowsException() {
            builtBankDetail.setUserId("invalid-uuid");

            MeedlException ex = assertThrows(MeedlException.class,
                    () -> bankDetailService.viewBankDetail(builtBankDetail));

            assertTrue(ex.getMessage().contains("Please identify user viewing bank details"));
            verifyNoInteractions(userIdentityOutputPort, bankDetailOutputPort);
        }

    @Test
    void viewBankDetail_WhenUserNotFound_ThrowsWrappedException() throws MeedlException {
        doThrow(new MeedlException("User not found"))
                .when(userIdentityOutputPort).findById(builtBankDetail.getUserId());

        MeedlException ex = assertThrows(MeedlException.class,
                () -> bankDetailService.viewBankDetail(builtBankDetail));

        assertEquals("Unable to identify user view bank details. Contact admin.", ex.getMessage());
        verify(userIdentityOutputPort, times(1)).findById(builtBankDetail.getUserId());
        verifyNoInteractions(bankDetailOutputPort);
    }

    @Test
    void viewBankDetail_WithValidData_ReturnsBankDetail() throws MeedlException {
        BankDetail expected = BankDetail.builder()
                .id(builtBankDetail.getId())
                .userId(builtBankDetail.getUserId())
                .bankName("Zenith")
                .bankNumber("9876543210")
                .build();
        Financier financier = Financier.builder()
                .id(UUID.randomUUID().toString())
                .build();

        when(financierOutputPort.findFinancierByUserId(any()))
                .thenReturn(financier); // doesn't matter, just no exception

        when(financierBankDetailOutputPort.findApprovedBankDetailByFinancierId(financier))
                .thenReturn(FinancierBankDetail.builder().bankDetail(expected).financier(financier).build());
        when(userIdentityOutputPort.findById(builtBankDetail.getUserId()))
                .thenReturn(UserIdentity.builder().role(IdentityRole.FINANCIER).build()); // doesn't matter, just no exception

        BankDetail result = bankDetailService.viewBankDetail(builtBankDetail);

        assertNotNull(result);
        assertEquals("Zenith", result.getBankName());
        assertEquals("9876543210", result.getBankNumber());
        verify(userIdentityOutputPort, times(1)).findById(builtBankDetail.getUserId());
    }

}
