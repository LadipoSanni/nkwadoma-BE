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
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import java.math.*;

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
    private LoanReferral loanReferral;
    private Loanee loanee;
    private LoaneeLoanDetail loaneeLoanDetail;
    private UserIdentity userIdentity;

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
            loanee = loaneeOutputPort.save(loanee);
            assertNotNull(loanee);
            UserIdentity foundUserIdentity = userIdentityOutputPort.findByEmail(loanee.getUserIdentity().getEmail());
            assertNotNull(foundUserIdentity);
        } catch (MeedlException e) {
            log.error("", e);
        }
    }

    @Test
    void saveLoanReferral() {
        loanReferral = LoanReferral.builder().loanee(loanee).
                loanReferralStatus(LoanReferralStatus.ACCEPTED).build();
//        loanReferral = new LoanReferral("LoanReferral1", "John Doe", "1234567890", "Email@example.com", "Loan Application");
        LoanReferral savedLoanReferral = loanReferralOutputPort.saveLoanReferral(loanReferral);
        assertNotNull(savedLoanReferral);
        assertNotNull(savedLoanReferral.getId());
    }

    @AfterAll
    void tearDown() {}
}