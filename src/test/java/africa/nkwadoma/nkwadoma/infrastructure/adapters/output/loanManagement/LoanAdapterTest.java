package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoaneeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.identity.UserEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
class LoanAdapterTest {
    @Autowired
    private LoanOutputPort loanOutputPort;
    @Autowired
    private LoaneeRepository loaneeRepository;
    @Autowired
    private UserEntityRepository userEntityRepository;
    private final String testId = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
    private Loan loan;
    private String savedLoanId;
    private String userId;
    private String loaneeId;
    @BeforeEach
    public void setUp(){

        UserEntity userEntity = new UserEntity();
        userEntity.setId(testId);
        userEntity = userEntityRepository.save(userEntity);
        userId = userEntity.getId();

        LoaneeEntity loaneeEntity = new LoaneeEntity();
        loaneeEntity.setUserIdentity(userEntity);
        loaneeEntity = loaneeRepository.save(loaneeEntity);
        loaneeId = loaneeEntity.getId();

        Loanee loanee = new Loanee();
        loanee.setId(loaneeEntity.getId());
        loanee.setUserIdentity(UserIdentity.builder().id(userEntity.getId()).build());

        loan = new Loan();
        loan.setLoanAccountId("account id");
        loan.setLoanee(Loanee.builder().id(loaneeEntity.getId()).build());
        loan.setStartDate(LocalDateTime.now());
        loan.setLoanee(loanee);

    }
    @Test
    void saveLoan() throws MeedlException {
        Loan savedLoan = null;
        try {
            savedLoan = loanOutputPort.save(loan);
            savedLoanId = savedLoan.getId();
            log.info("Saved loan: {} ", savedLoan.getId());
        } catch (MeedlException e) {
            log.error("Error saving loan {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

        assertNotNull(savedLoan);
        assertNotNull(savedLoan.getId());
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
    void findLoanById(String id){
        assertThrows(MeedlException.class,()->loanOutputPort.findLoanById(id));
    }
    @AfterEach
    void tearDown() {
        if (StringUtils.isNotEmpty(savedLoanId)) {
            loanOutputPort.deleteById(savedLoanId);
        }
        if (StringUtils.isNotEmpty(loaneeId)) {
            loaneeRepository.deleteById(loaneeId);
        }
    }
}