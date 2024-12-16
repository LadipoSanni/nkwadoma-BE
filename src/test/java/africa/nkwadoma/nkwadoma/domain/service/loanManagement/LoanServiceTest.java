package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
import africa.nkwadoma.nkwadoma.test.data.TestData;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class LoanServiceTest {
    @InjectMocks
    private LoanService loanService;
    @Mock
    private LoanReferralOutputPort loanReferralOutputPort;
    @Mock
    private LoanRequestMapper loanRequestMapper;
    @Mock
    private LoanRequestOutputPort loanRequestOutputPort;
    private LoanReferral loanReferral;
    private LoanRequest loanRequest;
    private Loan loan;
    private UserIdentity userIdentity;
    private String testId = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";

    @BeforeEach
    void setUp() {
        userIdentity = TestData.createTestUserIdentity("test@example.com");
        LoaneeLoanDetail loaneeLoanDetail = TestData.createTestLoaneeLoanDetail();
        Loanee loanee = TestData.createTestLoanee(userIdentity, loaneeLoanDetail);

        loanReferral = LoanReferral.builder().id(testId).loanee(loanee).
                loanReferralStatus(LoanReferralStatus.ACCEPTED).build();
        Vendor vendor = TestData.createTestVendor("Large vendor");
        LoanProduct loanProduct = TestData.buildTestLoanProduct("Test Loan Product - unit testing within application", vendor);

        loanRequest = TestData.buildLoanRequest(loanee, loaneeLoanDetail);
        loanRequest.setLoanProductId(loanProduct.getId());

        loan = TestData.createTestLoan(loanee);
    }

    @Test
    void createLoanRequest() {
        try {
            when(loanRequestOutputPort.save(loanRequest)).thenReturn(loanRequest);
            LoanRequest createdLoanRequest = loanService.createLoanRequest(loanRequest);

            verify(loanRequestOutputPort, times(1)).save(loanRequest);
            assertNotNull(createdLoanRequest);
        } catch (MeedlException e) {
            log.error("", e);
        }
    }

    @Test
    void createLoanRequestWithNullLoanReferralStatus() {
        loanRequest.setLoanReferralStatus(null);
        assertThrows(MeedlException.class, ()-> loanService.createLoanRequest(loanRequest));
    }

    @Test
    void createLoanRequestWithNonAcceptedLoanReferralStatus() {
        loanRequest.setLoanReferralStatus(LoanReferralStatus.DECLINED);
        assertThrows(MeedlException.class, ()-> loanService.createLoanRequest(loanRequest));
    }

    @Test
    void createNullLoanRequest() {
        assertThrows(MeedlException.class, ()-> loanService.createLoanRequest(null));
    }

    @Test
    void createLoanRequestWithNullLoanee() {
        loanRequest.setLoanee(null);
        assertThrows(MeedlException.class, ()-> loanService.createLoanRequest(loanRequest));
    }

    @Test
    void createLoanRequestWithNullLoanRequestStatus() {
        loanRequest.setStatus(null);
        assertThrows(MeedlException.class, ()-> loanService.createLoanRequest(loanRequest));
    }

    @Test
    void createLoanRequestWithNullLoaneeLoanDetail() {
        loanRequest.getLoanee().getLoaneeLoanDetail().setAmountRequested(null);
        assertThrows(MeedlException.class, ()-> loanService.createLoanRequest(loanRequest));
    }

    @Test
    void viewLoanReferral() {
        LoanReferral foundLoanReferral = null;
        try {
            when(loanReferralOutputPort.findLoanReferralByUserId(
                    loanReferral.getLoanee().getUserIdentity().getId())).thenReturn(List.of(loanReferral));
            when(loanReferralOutputPort.
                    findLoanReferralById(loanReferral.getId())).thenReturn(Optional.ofNullable(loanReferral));
            foundLoanReferral = loanService.viewLoanReferral(loanReferral);
            assertNotNull(foundLoanReferral);
            verify(loanReferralOutputPort, times(1)).
                    findLoanReferralByUserId(foundLoanReferral.getLoanee().getUserIdentity().getId());
        } catch (MeedlException e) {
            log.error("Error getting loan referral", e);
        }
        assertNotNull(foundLoanReferral);
    }

    @Test
    void viewLoanReferralWithNullInput() {
        assertThrows(MeedlException.class, () -> loanService.viewLoanReferral(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"     96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f",
            "96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f      ",
            "    96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f   "}
    )
    void viewLoanReferralWithTrailingAndLeadingSpaces(String loanReferralId) {
        loanReferral.setId(loanReferralId);
        assertThrows(MeedlException.class, ()->loanService.viewLoanReferral(loanReferral));
    }

    @Test
    void viewLoanReferralWithNullId() {
        loanReferral.setId(null);
        assertThrows(MeedlException.class, ()->loanService.viewLoanReferral(loanReferral));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void viewLoanReferralByIdWithSpaces(String loanReferralId) {
        loanReferral.setId(loanReferralId);
        assertThrows(MeedlException.class, ()->loanService.viewLoanReferral(loanReferral));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid id", "89954"})
    void viewLoanReferralByNonUUID(String id) {
        loanReferral.setId(id);
        assertThrows(MeedlException.class, ()->loanService.viewLoanReferral(loanReferral));
    }

    @Test
    void acceptLoanReferral() {
        LoanReferral referral = null;
        try {
//            when(loanReferralOutputPort.findLoanReferralByUserId(anyString())).thenReturn(List.of(loanReferral));
            when(loanReferralOutputPort.findById(loanReferral.getId())).thenReturn(loanReferral);
            when(loanRequestMapper.mapLoanReferralToLoanRequest(loanReferral)).thenReturn(loanRequest);
            when(loanService.createLoanRequest(loanRequest)).thenReturn(loanRequest);
            when(loanReferralOutputPort.saveLoanReferral(loanReferral)).thenReturn(loanReferral);
            referral = loanService.respondToLoanReferral(loanReferral);
        } catch (MeedlException e) {
            log.error(e.getMessage(), e);
        }
        assertNotNull(referral);
        assertEquals(LoanReferralStatus.AUTHORIZED, referral.getLoanReferralStatus());
    }

    @Test
    void acceptLoanReferralWithNullLoaneeAdditionalDetails() {
        loanReferral.getLoanee().getUserIdentity().setAlternateContactAddress(null);
        loanReferral.getLoanee().getUserIdentity().setAlternateEmail(null);
        loanReferral.getLoanee().getUserIdentity().setAlternatePhoneNumber(null);
        assertThrows(MeedlException.class, () -> loanService.respondToLoanReferral(loanReferral));
    }

    @Test
    void acceptNullLoanReferral() {
        assertThrows(MeedlException.class, ()-> loanService.respondToLoanReferral(null));
    }

    @Test
    void startLoanWithNull() {
        assertThrows(MeedlException.class, ()-> loanService.startLoan(null));
    }

    @ParameterizedTest
    @ValueSource(strings={StringUtils.EMPTY, StringUtils.SPACE, "invalid uuid"})
    void startLoanWithInvalidId(String loaneeId) {
        loan.setLoaneeId(loaneeId);
        assertThrows(MeedlException.class, ()-> loanService.startLoan(null));
    }

    @Test
    void acceptLoanReferralWithNullLoanReferralId() {
        loanReferral.setId(null);
        try {
            doThrow(LoanException.class).when(loanReferralOutputPort).findById(loanReferral.getId());
        } catch (LoanException e) {
            log.error("",e);
        }
        assertThrows(LoanException.class, ()-> loanService.respondToLoanReferral(loanReferral));
    }
}
