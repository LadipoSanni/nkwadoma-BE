package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.wallet;

import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.BankDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.FinancierBankDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.FinancierBankDetail;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
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
class FinancierBankDetailAdapterTest {
    @Autowired
    private FinancierBankDetailOutputPort financierBankDetailOutputPort;

    private Financier financier;
    private BankDetail bankDetail;
    private String financierBankDetailId;
    private String bankDetailId ;
    private String financierId;
    @Autowired
    private FinancierOutputPort financierOutputPort;
    @Autowired
    private BankDetailOutputPort bankDetailOutputPort;

    @BeforeAll
    void setup() {
        financier = Financier.builder()
                .id(UUID.randomUUID().toString())
                .name("Test Org")
                .email("mak@email.com")
                .build();
        bankDetail = BankDetail.builder()
                .bankName("Test Account")
                .bankNumber("1234567890")
                .activationStatus(ActivationStatus.APPROVED)
                .build();
        try {

            bankDetail = bankDetailOutputPort.save(bankDetail);
            Financier savedFinancier = financierOutputPort.save(financier);
            bankDetailId = bankDetail.getId();
            financierId = savedFinancier.getId();
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    @Order(1)
    void saveFinancierBankDetail() throws MeedlException {
        financier.setId(financierId);
        bankDetail.setId(bankDetailId);
        FinancierBankDetail financierBankDetail = FinancierBankDetail.builder()
                .financier(financier)
                .bankDetail(bankDetail)
                .build();

        FinancierBankDetail savedFinancierBankDetail = financierBankDetailOutputPort.save(financierBankDetail);
        financierBankDetailId = savedFinancierBankDetail.getId();
        log.info("Id of organization bank detail {}", financierId);

        assertThat(savedFinancierBankDetail.getFinancier().getId()).isEqualTo(financier.getId());
        assertThat(savedFinancierBankDetail.getBankDetail().getId()).isEqualTo(bankDetail.getId());

        List<BankDetail> bankDetails = financierBankDetailOutputPort.findAllBankDetailOfFinancier(financier);
        assertThat(bankDetails).hasSize(1);
    }

    @Test
    @Order(2)
    void findApprovedBankDetailByFinancierId() throws MeedlException {

        log.info("Organization id {}", financier.getId());
        FinancierBankDetail foundFinancierBankDetail = financierBankDetailOutputPort.findApprovedBankDetailByFinancierId(financier);

        assertThat(foundFinancierBankDetail).isNotNull();
        assertThat(foundFinancierBankDetail.getBankDetail().getId()).isEqualTo(bankDetail.getId());
    }

    @Test
    @Order(3)
    void findAllBankDetailOfFinancier() throws MeedlException {

        List<BankDetail> results = financierBankDetailOutputPort.findAllBankDetailOfFinancier(financier);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(bankDetail.getId());
    }

    @AfterAll
    void tearDown() throws MeedlException {
        financierBankDetailOutputPort.deleteById(financierBankDetailId);
        financierOutputPort.delete(financierId);
        bankDetailOutputPort.deleteById(bankDetailId);
        log.info("Deleted all after test. FinancierBankDetail");
    }
}