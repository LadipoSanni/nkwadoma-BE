package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import africa.nkwadoma.nkwadoma.test.data.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.data.domain.*;

import java.math.*;
import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class LoanRequestAdapterTest {
    @Autowired
    private LoanRequestOutputPort loanRequestOutputPort;
    @Autowired
    private LoanReferralOutputPort loanReferralOutputPort;
    @Autowired
    private LoaneeOutputPort loaneeOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    @Autowired
    private LoanRequestRepository loanRequestRepository;
    private LoanReferral loanReferral;
    private LoanRequest loanRequest;
    private LoaneeLoanDetail loaneeLoanDetail;
    private Loanee loanee;
    private String loaneeId;
    private String loaneeLoanDetailId;
    private String loanReferralId;
    private String userId;
    private String loanRequestId;

    @BeforeAll
    void setUp() {
        UserIdentity userIdentity = UserIdentity.builder().id("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").firstName("Adeshina").
                lastName("Qudus").email("test@example.com").role(IdentityRole.LOANEE).
                createdBy("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").build();

        loanee = Loanee.builder().userIdentity(userIdentity).
                cohortId("3a6d1124-1349-4f5b-831a-ac269369a90f").createdBy(userIdentity.getCreatedBy()).
                loaneeLoanDetail(LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(9000000.00)).
                        initialDeposit(BigDecimal.valueOf(3000000.00)).build()).build();

        try {
            UserIdentity savedUserIdentity = userIdentityOutputPort.save(loanee.getUserIdentity());
            userId = savedUserIdentity.getId();

            loaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loanee.getLoaneeLoanDetail());
            loaneeLoanDetailId = loaneeLoanDetail.getId();

            loanee.setLoaneeLoanDetail(loaneeLoanDetail);
            loanee.setUserIdentity(savedUserIdentity);
            loanee = loaneeOutputPort.save(loanee);
            assertNotNull(loanee);
            loaneeId = loanee.getId();

            loanReferral = new LoanReferral();
            loanReferral.setLoanee(loanee);
            loanReferral.setLoanReferralStatus(LoanReferralStatus.ACCEPTED);
            loanReferral = loanReferralOutputPort.saveLoanReferral(loanReferral);
            assertNotNull(loanReferral);
            loanReferralId = loanReferral.getId();
        } catch (MeedlException e) {
            log.error("", e);
        }
    }

    @BeforeEach
    void init() {
        loanRequest = new LoanRequest();
        loanRequest.setLoanAmountRequested(loanReferral.getLoanee().getLoaneeLoanDetail().getAmountRequested());
        loanRequest.setStatus(LoanRequestStatus.APPROVED);
        loanRequest.setReferredBy("Brown Hills Institute");
        loanee.setLoaneeLoanDetail(loaneeLoanDetail);
        loanRequest.setLoanee(loanee);
        loanRequest.setDateTimeApproved(LocalDateTime.now());
    }

    @AfterEach
    void tearDown() {
        if (StringUtils.isNotEmpty(loanRequestId)) {
            Optional<LoanRequestEntity> loanRequestEntity = loanRequestRepository.findById(loanRequestId);
            loanRequestEntity.ifPresent(requestEntity -> loanRequestRepository.delete(requestEntity));
        }
    }

    @Test
    void save() {
        LoanRequest savedLoanRequest = null;
        try {
            savedLoanRequest = loanRequestOutputPort.save(loanRequest);
        } catch (MeedlException e) {
            log.error("", e);
        }
        assertNotNull(savedLoanRequest);
        assertNotNull(savedLoanRequest.getId());
        loanRequestId = savedLoanRequest.getId();
    }

    @Test
    void saveNullLoanRequest() {
        assertThrows(MeedlException.class, ()->loanRequestOutputPort.save(null));
    }

    @Test
    void saveLoanRequestWithNullLoanee() {
        loanRequest.setLoanee(null);
        assertThrows(MeedlException.class, ()->loanRequestOutputPort.save(loanRequest));
    }

    @Test
    void saveLoanRequestWithNullLoanAmountRequested() {
        loanRequest.setLoanAmountRequested(null);
        assertThrows(MeedlException.class, ()->loanRequestOutputPort.save(loanRequest));
    }

    @Test
    void saveLoanRequestWithNullDateTimeApproved() {
        loanRequest.setDateTimeApproved(null);
        assertThrows(MeedlException.class, ()->loanRequestOutputPort.save(loanRequest));
    }

    @Test
    void saveLoanRequestWithNullLoanRequestStatus() {
        loanRequest.setStatus(null);
        assertThrows(MeedlException.class, ()->loanRequestOutputPort.save(loanRequest));
    }

    @Test
    void saveLoanRequestWithNullLoaneeLoanDetail() {
        loanRequest.getLoanee().setLoaneeLoanDetail(null);
        assertThrows(MeedlException.class, ()->loanRequestOutputPort.save(loanRequest));
    }

    @Test
    void viewAllLoanRequests() {
        UserIdentity john = TestData.createTestUserIdentity("john@example.com");
        loanee.setUserIdentity(john);
        loanee.setLoaneeLoanDetail(loaneeLoanDetail);
        LoanRequest request = new LoanRequest();
        request.setLoanAmountRequested(loanReferral.getLoanee().getLoaneeLoanDetail().getAmountRequested());
        request.setStatus(LoanRequestStatus.APPROVED);
        request.setReferredBy("Semicolon");
        request.setLoanee(loanee);
        request.setDateTimeApproved(LocalDateTime.now());
        try {
            LoanRequest savedLoanRequest = loanRequestOutputPort.save(loanRequest);
            LoanRequest savedRequest = loanRequestOutputPort.save(request);

            Page<LoanRequest> loanRequests = loanRequestOutputPort.viewAll(10, 0);

            assertNotNull(loanRequests.getContent());
            assertEquals(loanRequests.getContent(), List.of(savedRequest, savedLoanRequest));
        } catch (MeedlException e) {
            log.error("", e);
        }
    }


    @AfterAll
    void cleanUp() {
        try {
            loanReferralOutputPort.deleteLoanReferral(loanReferralId);
            loaneeOutputPort.deleteLoanee(loaneeId);
            userIdentityOutputPort.deleteUserById(userId);
            loaneeLoanDetailsOutputPort.delete(loaneeLoanDetailId);
        } catch (MeedlException e) {
            log.error("Exception occurred: ", e);
        }
    }
}