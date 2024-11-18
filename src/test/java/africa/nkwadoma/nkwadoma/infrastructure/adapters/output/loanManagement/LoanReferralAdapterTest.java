package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import java.math.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@SpringBootTest
class LoanReferralAdapterTest {
    @Autowired
    private LoanReferralOutputPort loanReferralOutputPort;
    @Autowired
    private LoaneeOutputPort loaneeOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    private LoanReferral loanReferral;
    private Loanee loanee;
    private String loaneeId;
    private UserIdentity userIdentity;
    private String loaneeLoanDetailId;
    private String loanReferralId;
    private String userId;

    @BeforeAll
    void setUp() {
        userIdentity = UserIdentity.builder().id("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").firstName("Adeshina").
                lastName("Qudus").email("test@example.com").role(IdentityRole.LOANEE).
                createdBy("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").build();

        loanee = Loanee.builder().userIdentity(userIdentity).
                cohortId("3a6d1124-1349-4f5b-831a-ac269369a90f").createdBy(userIdentity.getCreatedBy()).
                loaneeLoanDetail(LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(9000000.00)).
                        initialDeposit(BigDecimal.valueOf(3000000.00)).build()).build();

        try {
            UserIdentity savedUserIdentity = userIdentityOutputPort.save(loanee.getUserIdentity());
            userId = savedUserIdentity.getId();

            LoaneeLoanDetail savedLoaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loanee.getLoaneeLoanDetail());
            loaneeLoanDetailId = savedLoaneeLoanDetail.getId();

            loanee.setLoaneeLoanDetail(savedLoaneeLoanDetail);
            loanee.setUserIdentity(savedUserIdentity);

            loanee = loaneeOutputPort.save(loanee);
            assertNotNull(loanee);
            loaneeId = loanee.getId();

            loanReferral = new LoanReferral();
            loanReferral.setLoanee(loanee);
            loanReferral.setLoanReferralStatus(LoanReferralStatus.ACCEPTED);
            LoanReferral savedLoanReferral = loanReferralOutputPort.saveLoanReferral(loanReferral);
            assertNotNull(savedLoanReferral);
            loanReferralId = savedLoanReferral.getId();
        } catch (MeedlException e) {
            log.error("", e);
        }
    }


    @Test
    void viewLoanReferral() {
        Optional<LoanReferral> referral = Optional.empty();
        try {
            referral = loanReferralOutputPort.findLoanReferralById(loanReferralId);
        } catch (MeedlException e) {
            log.error("Error getting loan referral", e);
        }
        assertNotNull(referral);
    }

    @Test
    void viewLoanReferralWithTrailingAndLeadingSpaces() {
        Optional<LoanReferral> referral = Optional.empty();
        try {
            referral = loanReferralOutputPort.findLoanReferralById(loanReferralId.concat(StringUtils.SPACE));
        } catch (MeedlException e) {
            log.error("Error getting loan referral", e);
        }
        assertNotNull(referral);
    }

    @Test
    void viewLoanReferralWithNullId() {
        assertThrows(MeedlException.class, ()->loanReferralOutputPort.findLoanReferralById(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void viewLoanReferralByIdWithSpaces(String loanReferralId) {
        assertThrows(MeedlException.class, ()->loanReferralOutputPort.findLoanReferralById(loanReferralId));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid id", "74567"})
    void viewLoanReferralByNonUUID(String loanReferralId) {
        assertThrows(MeedlException.class, ()->loanReferralOutputPort.findLoanReferralById(loanReferralId));
    }

    @AfterAll
    void tearDown() {
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
 