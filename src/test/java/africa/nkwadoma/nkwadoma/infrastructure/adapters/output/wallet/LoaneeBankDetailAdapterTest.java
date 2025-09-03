package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.wallet;

import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.BankDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.LoaneeBankDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.LoaneeBankDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class LoaneeBankDetailAdapterTest {
    @Autowired
    private LoaneeBankDetailOutputPort loaneeBankDetailOutputPort;

    private Loanee loanee;
    private BankDetail bankDetail;
    private UserIdentity userIdentity;
    private String loaneeBankDetailId;
    private String bankDetailId ;
    private String loaneeId;
    @Autowired
    private LoaneeOutputPort loaneeOutputPort;
    @Autowired
    private BankDetailOutputPort bankDetailOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;

    @BeforeAll
    void setup() {
        userIdentity = TestData.createTestUserIdentity("woker@gmial.com");
        try {
            userIdentityOutputPort.save(userIdentity);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        loanee = Loanee.builder()
                .id(UUID.randomUUID().toString())
                .userIdentity(userIdentity)
                .build();
        bankDetail = BankDetail.builder()
                .bankName("Test Account")
                .bankNumber("1234567890")
                .activationStatus(ActivationStatus.APPROVED)
                .build();
        try {

            bankDetail = bankDetailOutputPort.save(bankDetail);
            Loanee savedLoanee = loaneeOutputPort.save(loanee);
            bankDetailId = bankDetail.getId();
            loaneeId = savedLoanee.getId();
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    @Order(1)
    void saveLoaneeBankDetail() throws MeedlException {
        loanee.setId(loaneeId);
        bankDetail.setId(bankDetailId);
        LoaneeBankDetail loaneeBankDetail = LoaneeBankDetail.builder()
                .loanee(loanee)
                .bankDetail(bankDetail)
                .build();

        LoaneeBankDetail savedLoaneeLoanDetail = loaneeBankDetailOutputPort.save(loaneeBankDetail);
        loaneeBankDetailId = savedLoaneeLoanDetail.getId();
        log.info("Id of loanee bank detail {}", loaneeId);

        assertThat(savedLoaneeLoanDetail.getLoanee().getId()).isEqualTo(loanee.getId());
        assertThat(savedLoaneeLoanDetail.getBankDetail().getId()).isEqualTo(bankDetail.getId());

        List<BankDetail> bankDetails = loaneeBankDetailOutputPort.findAllBankDetailOfLoanee(loanee);
        assertThat(bankDetails).hasSize(1);
    }

    @Test
    @Order(2)
    void findApprovedBankDetailByLoaneeId() throws MeedlException {

        log.info("Loanee id {}", loanee.getId());
        LoaneeBankDetail foundLoaneeLoanDetail = loaneeBankDetailOutputPort.findApprovedBankDetailByLoaneeId(loanee);

        assertThat(foundLoaneeLoanDetail).isNotNull();
        assertThat(foundLoaneeLoanDetail.getBankDetail().getId()).isEqualTo(bankDetail.getId());
    }

    @Test
    @Order(3)
    void findAllBankDetailOfLoanee() throws MeedlException {

        List<BankDetail> results = loaneeBankDetailOutputPort.findAllBankDetailOfLoanee(loanee);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(bankDetail.getId());
    }

    @AfterAll
    void tearDown() throws MeedlException {
        loaneeBankDetailOutputPort.deleteById(loaneeBankDetailId);
        loaneeOutputPort.deleteLoanee(loaneeId);
        userIdentityOutputPort.deleteUserById(userIdentity.getId());
        bankDetailOutputPort.deleteById(bankDetailId);
        log.info("Deleted all after test. FinancierBankDetail");
    }
}