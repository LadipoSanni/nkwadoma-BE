package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
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
class LoanServiceTest {
    @InjectMocks
    private LoanService loanService;
    @Mock
    private LoanReferralOutputPort loanReferralOutputPort;
    @Mock
    private LoanRequestOutputPort loanRequestOutputPort;
    @Mock
    private LoanRequestMapper loanRequestMapper;
    private LoanReferral loanReferral;
    private LoanRequest loanRequest;

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
        loanRequest.setLoanAmountRequested(loanReferral.getLoanee().getLoaneeLoanDetail().getAmountRequested());
        loanRequest.setStatus(LoanRequestStatus.APPROVED);
        loanRequest.setLoanReferralStatus(LoanReferralStatus.ACCEPTED);
        loanRequest.setReferredBy("Brown Hills Institute");
        loanee.setLoaneeLoanDetail(loaneeLoanDetail);
        loanRequest.setLoanee(loanee);
        loanRequest.setDateTimeApproved(LocalDateTime.now());
    }


    @Test
    void viewLoanReferral() {
        LoanReferral foundLoanReferral = null;
        try {
            when(loanReferralOutputPort.findLoanReferralById(loanReferral.getId()))
                    .thenReturn(Optional.ofNullable(loanReferral));
            foundLoanReferral = loanService.viewLoanReferral(loanReferral);

            verify(loanReferralOutputPort, times(1)).
                    findLoanReferralById(foundLoanReferral.getId());
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
        LoanReferral foundLoanReferral = null;
        try {
            loanReferral.setId(loanReferralId);
            when(loanReferralOutputPort.findLoanReferralById(loanReferral.getId().trim()))
                    .thenReturn(Optional.ofNullable(loanReferral));
            foundLoanReferral = loanService.viewLoanReferral(loanReferral);
        } catch (MeedlException e) {
            log.error("Error getting loan referral", e);
        }
        assertNotNull(foundLoanReferral);
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
    void viewLoanReferralByNonUUID(String loanReferralId) {
        loanReferral.setId(loanReferralId);
        assertThrows(MeedlException.class, ()->loanService.viewLoanReferral(loanReferral));
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
    void createLoanRequestWithNullLoanAmountRequested() {
        loanRequest.setLoanAmountRequested(null);
        assertThrows(MeedlException.class, ()-> loanService.createLoanRequest(loanRequest));
    }

    @Test
    void createLoanRequestWithNullDateTimeApproved() {
        loanRequest.setDateTimeApproved(null);
        assertThrows(MeedlException.class, ()-> loanService.createLoanRequest(loanRequest));
    }

    @Test
    void createLoanRequestWithNullLoanRequestStatus() {
        loanRequest.setStatus(null);
        assertThrows(MeedlException.class, ()-> loanService.createLoanRequest(loanRequest));
    }

    @Test
    void createLoanRequestWithNullLoaneeLoanDetail() {
        loanRequest.getLoanee().setLoaneeLoanDetail(null);
        assertThrows(MeedlException.class, ()-> loanService.createLoanRequest(loanRequest));
    }

    @Test
    void acceptLoanReferral() {
        LoanReferral referral = null;
        try {
            when(loanReferralOutputPort.findLoanReferralById(loanReferral.getId())).thenReturn(Optional.of(loanReferral));
            when(loanRequestMapper.mapLoanReferralToLoanRequest(loanReferral)).thenReturn(loanRequest);
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
    void acceptLoanReferralWithNullLoanReferralId() {
        loanReferral.setId(null);
        assertThrows(MeedlException.class, ()-> loanService.respondToLoanReferral(loanReferral));
    }

    @Test
    void viewAllLoanRequests() {
        try {
            when(loanRequestOutputPort.viewAll(0, 10)).
                    thenReturn(new PageImpl<>(List.of(loanRequest)));
            Page<LoanRequest> loanRequests = loanService.viewAllLoanRequests(loanRequest);

            verify(loanRequestOutputPort, times(1)).viewAll(0, 10);
            assertNotNull(loanRequests.getContent());
        } catch (MeedlException e) {
            log.error(e.getMessage(), e);
        }
    }
}