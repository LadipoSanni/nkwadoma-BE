package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.RepaymentHistoryOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.testUtilities.TestUtils;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class RepaymentHistoryServiceTest {
    @InjectMocks
    private RepaymentHistoryService repaymentHistoryService;
    @Mock
    private CohortUseCase cohortUseCase;
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private RepaymentHistoryOutputPort repaymentHistoryOutputPort;
    @Mock
    private LoaneeOutputPort loaneeOutputPort;
    private RepaymentHistory repaymentHistory;
    private List<RepaymentHistory> repaymentHistories;
    private final String actorId = TestUtils.generateRandomUUID();
    private final String cohortId = TestUtils.generateRandomUUID();
    private LoanBook loanBook;
    private UserIdentity userIdentity;
    private Loanee loanee;
    private LoaneeLoanDetail loaneeLoanDetail;
    private int pageSize = 10;
    private int pageNumber = 0;

    @BeforeEach
    void setUp() {
        loanBook = TestData.buildLoanBook("randomLink", cohortId);
        userIdentity = TestData.createTestUserIdentity("loanee24@gmail.com");
        loaneeLoanDetail = TestData.createTestLoaneeLoanDetail();
        loanee = TestData.createTestLoanee(userIdentity,loaneeLoanDetail);
        repaymentHistory = TestData.buildRepaymentHistory(loanBook.getCohort().getId());
        repaymentHistory.setLoanee(loanee);
        repaymentHistories = List.of(repaymentHistory);
        loanBook.setRepaymentHistories(repaymentHistories);
        loanBook.setActorId(actorId);
    }

    @Test
    void findAllRepaymentHistoryByPM_WithNullLoaneeIdReturnAllRepaymentHistory() {
        Page<RepaymentHistory> repaymentHistoryPage = null;
        try {
            repaymentHistory.setActorId(actorId);
            repaymentHistory.setLoaneeId(null);
            userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER);
            when(userIdentityOutputPort.findById(actorId)).thenReturn(userIdentity);
            when(repaymentHistoryOutputPort.findRepaymentHistoryAttachedToALoaneeOrAll(repaymentHistory,pageSize,pageNumber))
                    .thenReturn(new PageImpl<>(repaymentHistories));
            repaymentHistory.setLoaneeId(null);
            repaymentHistoryPage = repaymentHistoryService.findAllRepaymentHistory(repaymentHistory,pageSize,pageNumber);
        }catch (MeedlException meedlException) {
            assertEquals(repaymentHistoryPage.getContent().size(),repaymentHistories.size());
        }
    }   @Test
    void findAllRepaymentHistoryByMonth() {
        Page<RepaymentHistory> repaymentHistoryPage = null;
        try {
            repaymentHistory.setActorId(actorId);
            repaymentHistory.setMonth(LocalDateTime.now().getMonthValue());
            userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER);
            when(userIdentityOutputPort.findById(actorId)).thenReturn(userIdentity);
            when(repaymentHistoryOutputPort.findRepaymentHistoryAttachedToALoaneeOrAll(repaymentHistory,pageSize,pageNumber))
                    .thenReturn(new PageImpl<>(repaymentHistories));
            repaymentHistory.setLoaneeId(null);
            repaymentHistoryPage = repaymentHistoryService.findAllRepaymentHistory(repaymentHistory,pageSize,pageNumber);
        }catch (MeedlException meedlException) {
            assertEquals(repaymentHistoryPage.getContent().size(),repaymentHistories.size());
        }
    }   @Test
    void findAllRepaymentHistoryByYear() {
        Page<RepaymentHistory> repaymentHistoryPage = null;
        try {
            repaymentHistory.setActorId(actorId);
            repaymentHistory.setYear(LocalDateTime.now().getYear());
            userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER);
            when(userIdentityOutputPort.findById(actorId)).thenReturn(userIdentity);
            when(repaymentHistoryOutputPort.findRepaymentHistoryAttachedToALoaneeOrAll(repaymentHistory,pageSize,pageNumber))
                    .thenReturn(new PageImpl<>(repaymentHistories));
            repaymentHistory.setLoaneeId(null);
            repaymentHistoryPage = repaymentHistoryService.findAllRepaymentHistory(repaymentHistory,pageSize,pageNumber);
        }catch (MeedlException meedlException) {
            assertEquals(repaymentHistoryPage.getContent().size(),repaymentHistories.size());
        }
    }

    @Test
    void findAllRepaymentHistoryByPM_WithValidLoaneeIdReturnAllRepaymentHistoryAttachedToThatLoanee() {
        Page<RepaymentHistory> repaymentHistoryPage = null;
        String loaneeId = loanee.getId();
        repaymentHistory.setLoaneeId(loaneeId);
        repaymentHistory.setActorId(actorId);
        try {
            userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER);
            when(userIdentityOutputPort.findById(actorId)).thenReturn(userIdentity);
            when(repaymentHistoryOutputPort.findRepaymentHistoryAttachedToALoaneeOrAll(repaymentHistory,pageSize,pageNumber))
                    .thenReturn(new PageImpl<>(repaymentHistories));
            repaymentHistoryPage = repaymentHistoryService.findAllRepaymentHistory(repaymentHistory,pageSize,pageNumber);
        }catch (MeedlException meedlException) {
            assertEquals(repaymentHistoryPage.getContent().size(),repaymentHistories.size());
        }
    }

    @Test
    void findAllRepaymentHistoryByLoanee_ReturnAllRepaymentHistoryAttachedToThatLoanee() {
        Page<RepaymentHistory> repaymentHistoryPage = null;
        try {
            repaymentHistory.setActorId(actorId);
            userIdentity.setId(actorId);
            when(userIdentityOutputPort.findById(actorId)).thenReturn(userIdentity);
            when(loaneeOutputPort.findByUserId(userIdentity.getId())).thenReturn(Optional.of(loanee));
            repaymentHistory.setLoaneeId(loanee.getId());
            when(repaymentHistoryOutputPort.findRepaymentHistoryAttachedToALoaneeOrAll(repaymentHistory,pageSize,pageNumber))
                    .thenReturn(new PageImpl<>(repaymentHistories));
            repaymentHistoryPage = repaymentHistoryService.findAllRepaymentHistory(repaymentHistory,pageSize,pageNumber);
        }catch (MeedlException meedlException) {
            assertEquals(repaymentHistoryPage.getContent().size(),repaymentHistories.size());
        }
    }

    @Test
    void searchRepaymentHistoryByLoaneeName() {
        Page<RepaymentHistory> repaymentHistoryPage = null;
        try {
            repaymentHistory.setLoaneeName("d");
            when(repaymentHistoryOutputPort.searchRepaymemtHistoryByLoaneeName(repaymentHistory,pageSize,pageNumber))
                    .thenReturn(new PageImpl<>(repaymentHistories));
            repaymentHistoryPage = repaymentHistoryService.searchRepaymentHistory(repaymentHistory,pageSize,pageNumber);
        }catch (MeedlException meedlException) {
            assertEquals(repaymentHistoryPage.getContent().size(),repaymentHistories.size());
        }
    }

    @AfterEach
    void tearDown() {
    }
}