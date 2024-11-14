package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
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
    private LoanReferral loanReferral;
    private Loanee loanee;
    private UserIdentity userIdentity;
    private int pageNumber = 0;
    private int pageSize = 10;

    @BeforeEach
    void setUp() {
        userIdentity = UserIdentity.builder().id("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").firstName("Adeshina").
                lastName("Qudus").email("test@example.com").role(IdentityRole.LOANEE).
                createdBy("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").build();

        loanee = Loanee.builder().userIdentity(userIdentity).
                cohortId("3a6d1124-1349-4f5b-831a-ac269369a90f").createdBy(userIdentity.getCreatedBy()).
                loaneeLoanDetail(LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(9000000.00)).
                        initialDeposit(BigDecimal.valueOf(3000000.00)).build()).build();

        loanReferral = LoanReferral.builder().loanee(loanee).pageNumber(pageNumber).pageSize(pageSize).
                loanReferralStatus(LoanReferralStatus.ACCEPTED).build();
    }


    @Test
    void viewLoanReferral() {
        LoanReferral loanReferral = null;
        try {
            loanReferral = loanService.viewLoanReferral(loanReferral);

            verify(loanReferralOutputPort, times(1)).
                    findLoanReferralByLoaneeId(loanReferral.getLoanee().getUserIdentity().getId()
                    );
        } catch (MeedlException e) {
            log.error("Error getting loan referral", e);
        }

        assertNotNull(loanReferral);
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
    void viewLoanReferralWithTrailingAndLeadingSpaces(String userId) {
        LoanReferral loanReferral = null;
        try {
            loanReferral.getLoanee().getUserIdentity().setId(userId);
            loanReferral = loanService.viewLoanReferral(loanReferral);
        } catch (MeedlException e) {
            log.error("Error getting loan referral", e);
        }
        assertNotNull(loanReferral);
    }

    @Test
    void viewLoanReferralWithNullId() {
        loanReferral.getLoanee().getUserIdentity().setId(null);
        assertThrows(MeedlException.class, ()->loanService.viewLoanReferral(loanReferral));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void viewLoanReferralByIdWithSpaces(String loaneeId) {
        loanReferral.getLoanee().getUserIdentity().setId(loaneeId);
        assertThrows(MeedlException.class, ()->loanService.viewLoanReferral(loanReferral));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid id", "NON-UUID"})
    void viewLoanReferralByNonUUID(String loaneeId) {
        loanReferral.getLoanee().getUserIdentity().setId(loaneeId);
        assertThrows(MeedlException.class, ()->loanService.viewLoanReferral(loanReferral));
    }
}