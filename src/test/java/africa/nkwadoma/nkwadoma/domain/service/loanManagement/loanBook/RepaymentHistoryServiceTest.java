package africa.nkwadoma.nkwadoma.domain.service.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.loanBook.RepaymentHistoryOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.testUtilities.TestUtils;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    private RepaymentHistory repaymentHistory;
    private List<RepaymentHistory> repaymentHistories;
    private final String actorId = TestUtils.generateRandomUUID();
    private final String cohortId = TestUtils.generateRandomUUID();
    private LoanBook loanBook;

    @BeforeEach
    void setUp() {
        loanBook = TestData.buildLoanBook("randomLink", cohortId);
        repaymentHistory = TestData.buildRepaymentHistory(loanBook.getCohort().getId());
        repaymentHistories = List.of(repaymentHistory);
        loanBook.setRepaymentHistories(repaymentHistories);
        loanBook.setActorId(actorId);
    }

    @Test
    void saveRepaymentHistory() throws MeedlException {
        when(cohortUseCase.viewCohortDetails(actorId,cohortId)).thenReturn(new Cohort());
        when(repaymentHistoryOutputPort.save(repaymentHistory)).thenReturn(repaymentHistory);
        when(userIdentityOutputPort.findByEmail(repaymentHistory.getLoanee().getUserIdentity().getEmail()))
                .thenReturn(repaymentHistory.getLoanee().getUserIdentity());
        repaymentHistoryService.saveCohortRepaymentHistory(loanBook);
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "invalid-id"})
    void saveRepaymentHistoryInvalidCohortId(String cohortId){
        loanBook.getCohort().setId(cohortId);
        assertThrows(MeedlException.class, ()-> repaymentHistoryService.saveCohortRepaymentHistory(loanBook));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "invalid-id"})
    void saveRepaymentHistoryInvalidActorId(String actorId){
        loanBook.setActorId(actorId);
        assertThrows(MeedlException.class, ()-> repaymentHistoryService.saveCohortRepaymentHistory(loanBook));
    }
    @Test
    void saveRepaymentHistoryWithEmptyRepaymentHistory(){
        loanBook.setRepaymentHistories(null);
        assertThrows(MeedlException.class, ()->repaymentHistoryService.saveCohortRepaymentHistory(loanBook));
    }
    @Test
    void saveRepaymentHistoryOfNunExistingCohort() throws MeedlException {
        when(cohortUseCase.viewCohortDetails(actorId, cohortId)).thenThrow(MeedlException.class);
        assertThrows(MeedlException.class, ()-> repaymentHistoryService.saveCohortRepaymentHistory(loanBook));
    }
    @AfterEach
    void tearDown() {
    }
}