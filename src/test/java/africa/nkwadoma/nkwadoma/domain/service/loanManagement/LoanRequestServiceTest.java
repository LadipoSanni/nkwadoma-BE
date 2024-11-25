package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.data.domain.*;

import java.math.*;
import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@Slf4j
@ExtendWith(MockitoExtension.class)
class LoanRequestServiceTest {
    @InjectMocks
    private LoanRequestService loanRequestService;
    @Mock
    private LoanRequestOutputPort loanRequestOutputPort;
    private LoanRequest loanRequest;
    private LoanReferral loanReferral;

    @BeforeEach
    void setUp() {
        UserIdentity userIdentity = UserIdentity.builder().id("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").firstName("Adeshina").
                lastName("Qudus").email("test@example.com").role(IdentityRole.LOANEE).alternateEmail("alt276@example.com").
                alternatePhoneNumber("0986564534").alternateContactAddress("10, Onigbagbo Street, Mushin, Lagos State").
                createdBy("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").build();

        LoaneeLoanDetail loaneeLoanDetail = LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(9000000.00)).
                initialDeposit(BigDecimal.valueOf(3000000.00)).build();
        Loanee loanee = Loanee.builder().id("b1b832a2-5f73-46d8-a073-e5d812304a4b").userIdentity(userIdentity).
                cohortId("3a6d1124-1349-4f5b-831a-ac269369a90f").createdBy(userIdentity.getCreatedBy()).
                loaneeLoanDetail(loaneeLoanDetail).build();

        loanReferral = LoanReferral.builder().id("3a6d1124-1349-4f5b-831a-ac269369a90f").loanee(loanee).
                loanReferralStatus(LoanReferralStatus.ACCEPTED).build();

        loanRequest = new LoanRequest();
        loanRequest.setId("1100124b-92fd-405a-ac44-e2fca244bbea");
        loanRequest.setLoanAmountRequested(loanReferral.getLoanee().getLoaneeLoanDetail().getAmountRequested());
        loanRequest.setStatus(LoanRequestStatus.APPROVED);
        loanRequest.setLoanReferralStatus(LoanReferralStatus.ACCEPTED);
        loanRequest.setReferredBy("Brown Hills Institute");
        loanee.setLoaneeLoanDetail(loaneeLoanDetail);
        loanRequest.setLoanee(loanee);
        loanRequest.setDateTimeApproved(LocalDateTime.now());
    }

    @Test
    void createLoanRequest() {
        try {
            when(loanRequestOutputPort.save(loanRequest)).thenReturn(loanRequest);
            LoanRequest createdLoanRequest = loanRequestService.createLoanRequest(loanRequest);

            verify(loanRequestOutputPort, times(1)).save(loanRequest);
            assertNotNull(createdLoanRequest);
        } catch (MeedlException e) {
            log.error("", e);
        }
    }

    @Test
    void createLoanRequestWithNullLoanReferralStatus() {
        loanRequest.setLoanReferralStatus(null);
        assertThrows(MeedlException.class, ()-> loanRequestService.createLoanRequest(loanRequest));
    }

    @Test
    void createLoanRequestWithNonAcceptedLoanReferralStatus() {
        loanRequest.setLoanReferralStatus(LoanReferralStatus.DECLINED);
        assertThrows(MeedlException.class, ()-> loanRequestService.createLoanRequest(loanRequest));
    }

    @Test
    void createNullLoanRequest() {
        assertThrows(MeedlException.class, ()-> loanRequestService.createLoanRequest(null));
    }

    @Test
    void createLoanRequestWithNullLoanee() {
        loanRequest.setLoanee(null);
        assertThrows(MeedlException.class, ()-> loanRequestService.createLoanRequest(loanRequest));
    }

    @Test
    void createLoanRequestWithNullLoanAmountRequested() {
        loanRequest.setLoanAmountRequested(null);
        assertThrows(MeedlException.class, ()-> loanRequestService.createLoanRequest(loanRequest));
    }

    @Test
    void createLoanRequestWithNullLoanRequestStatus() {
        loanRequest.setStatus(null);
        assertThrows(MeedlException.class, ()-> loanRequestService.createLoanRequest(loanRequest));
    }

    @Test
    void createLoanRequestWithNullLoaneeLoanDetail() {
        loanRequest.getLoanee().setLoaneeLoanDetail(null);
        assertThrows(MeedlException.class, ()-> loanRequestService.createLoanRequest(loanRequest));
    }

    @Test
    void viewAllLoanRequests() {
        try {
            when(loanRequestOutputPort.viewAll(0, 10)).
                    thenReturn(new PageImpl<>(List.of(loanRequest)));
            Page<LoanRequest> loanRequests = loanRequestService.viewAllLoanRequests(loanRequest);

            verify(loanRequestOutputPort, times(1)).viewAll(0, 10);
            assertNotNull(loanRequests.getContent());
        } catch (MeedlException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    void viewLoanRequestById() {
        try {
            when(loanRequestOutputPort.findById(loanRequest.getId())).thenReturn(Optional.of(loanRequest));
            LoanRequest retrievedLoanRequest = loanRequestService.viewLoanRequestById(loanRequest);

            verify(loanRequestOutputPort, times(1)).findById(loanRequest.getId());
            assertNotNull(retrievedLoanRequest);
        } catch (MeedlException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    void viewNullLoanRequest() {
        assertThrows(MeedlException.class, ()-> loanRequestService.viewLoanRequestById(null));
    }

    @Test
    void viewLoanRequestWithNullId() {
        loanRequest.setId(null);
        assertThrows(MeedlException.class, ()-> loanRequestService.viewLoanRequestById(loanRequest));
    }

    @Test
    void viewLoanRequestWithNonExistingId() {
        loanRequest.setId("0d09dd7f-e6ed-49fd-85b8-dfaffcac9ea1");
        assertThrows(MeedlException.class, ()-> loanRequestService.viewLoanRequestById(loanRequest));
    }

    @ParameterizedTest
    @ValueSource(strings = {"36470395798", "sjgbnsvkh"})
    void viewLoanRequestWithNonUUID(String id) {
        loanRequest.setId(id);
        assertThrows(MeedlException.class, ()-> loanRequestService.viewLoanRequestById(loanRequest));
    }
}