package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.*;
import africa.nkwadoma.nkwadoma.test.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoanAdapterTest {
    @Autowired
    private LoanOutputPort loanOutputPort;
    @Autowired
    private LoaneeOutputPort loaneeOutputPort;
    @Autowired
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    private Loan loan;
    private LoaneeLoanAccount loaneeLoanAccount;
    private String savedLoanId;
    private String loaneeId;
    private String loanId;
    @Autowired
    private LoaneeLoanAccountPersistenceAdapter loaneeLoanAccountOutputPort;
    private String loaneeLoanAccountId;

    @BeforeAll
    public void setUp(){
        UserIdentity userIdentity = TestData.createTestUserIdentity("testuser@email.com");
        try {
            userIdentity = saveUserIdentity(userIdentity);
        } catch (MeedlException e) {
            log.error("Error saving user {}", e.getMessage());
            throw new RuntimeException(e);
        }
        LoaneeLoanDetail loaneeLoanDetail = new LoaneeLoanDetail();
        loaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
        Loanee loanee = TestData.createTestLoanee(userIdentity, loaneeLoanDetail);
        try {
            loanee = loaneeOutputPort.save(loanee);
        } catch (MeedlException e) {
            log.error("Error saving loanee {}", e.getMessage());
            throw new RuntimeException(e);
        }
        loaneeLoanAccount = TestData.createLoaneeLoanAccount(LoanStatus.AWAITING_DISBURSAL, AccountStatus.NEW, loanee.getId());
        try {
            LoaneeLoanAccount foundLoaneeAccount = loaneeLoanAccountOutputPort.findByLoaneeId(loanee.getId());
            if (ObjectUtils.isEmpty(foundLoaneeAccount)) {
                loaneeLoanAccount = loaneeLoanAccountOutputPort.save(loaneeLoanAccount);
                loaneeLoanAccountId = loaneeLoanAccount.getId();
            }
            else loaneeLoanAccountOutputPort.deleteLoaneeLoanAccount(foundLoaneeAccount.getId());
        } catch (MeedlException e) {
            log.error("Error saving loanee account", e);
        }
        loaneeId = loanee.getId();
        loan = TestData.createTestLoan(loanee);
        loanee.setUserIdentity(userIdentity);
        loan.setLoanee(loanee);
        loan.setStartDate(LocalDateTime.now());
        loan.setLoanAccountId(loaneeLoanAccountId);
    }

    @Test
    @Order(1)
    void saveLoan(){
        Loan savedLoan;
        try {
            savedLoan = loanOutputPort.save(loan);
            savedLoanId = savedLoan.getId();
            log.info("Saved loan: {} ", savedLoan.getId());
            loanId = savedLoan.getId();
        } catch (MeedlException e) {
            log.error("Error saving loan {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        assertNotNull(savedLoan);
        assertNotNull(savedLoan.getId());
        assertNotNull(savedLoan.getStartDate());
        assertEquals(loaneeLoanAccountId, savedLoan.getLoanAccountId());
        assertEquals(loaneeId, savedLoan.getLoaneeId());
    }
    @Test
    void saveLoanWithNull() {
        assertThrows(MeedlException.class, ()->loanOutputPort.save(null));
    }
    @Test
    void saveLoanWithNullLoanee() {
        loan.setLoanee(null);
        assertThrows(MeedlException.class, ()->loanOutputPort.save(loan));
    }
    @Test
    void saveLoanWithNullStartDate() {
        loan.setStartDate(null);
        assertThrows(MeedlException.class, ()->loanOutputPort.save(loan));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "invalid.id"})
    void findLoanByInvalidId(String id){
        assertThrows(MeedlException.class,()->loanOutputPort.findLoanById(id));
    }
    @Test
    @Order(2)
    void findLoanById() {
        Loan loan = null;
        try {
            log.info("loan id before finding : {}", loanId);
            loan = loanOutputPort.findLoanById(loanId);
            log.info("loan id after finding : {}", loan);
        } catch (MeedlException e) {
            log.error("Error getting loan {}", e.getMessage());
            throw new RuntimeException(e);
        }
        assertNotNull(loan);
        assertNotNull(loan.getId());
        assertEquals(loan.getId(), loanId);

    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "invalid.id"})
    void deleteLoanByInvalidId(String id){
        assertThrows(MeedlException.class,()->loanOutputPort.deleteById(id));
    }
    @AfterAll
    void tearDown() {
        deleteLoan();
    }

    private void deleteLoan() {
        if (StringUtils.isNotEmpty(loaneeLoanAccountId)) {
            try {
                loaneeLoanAccountOutputPort.deleteLoaneeLoanAccount(loaneeLoanAccountId);
            } catch (MeedlException e) {
                log.error("Error deleting loanee account", e);
            }
        }
        if (StringUtils.isNotEmpty(savedLoanId)) {
            try {
                loanOutputPort.deleteById(savedLoanId);
            } catch (MeedlException e) {
                log.error("Error deleting loan {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        if (StringUtils.isNotEmpty(loaneeId)) {
            try {
                loaneeOutputPort.deleteLoanee(loaneeId);

            } catch (MeedlException e) {
                log.error("Error deleting loanee {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    private UserIdentity saveUserIdentity(UserIdentity userIdentity) throws MeedlException {
        try {
            deleteLoan();
            userIdentityOutputPort.deleteUserById(userIdentity.getId());
        } catch (MeedlException e) {
            log.error("Error deleting user {}", e.getMessage());
        }
        userIdentity = userIdentityOutputPort.save(userIdentity);
        return userIdentity;
    }
}