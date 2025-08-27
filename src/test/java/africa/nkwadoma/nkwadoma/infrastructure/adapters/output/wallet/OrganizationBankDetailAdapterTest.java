package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.wallet;

import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.BankDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.OrganizationBankDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.OrganizationType;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.OrganizationBankDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class OrganizationBankDetailAdapterTest {


    @Autowired
    private OrganizationBankDetailOutputPort organizationBankDetailOutputPort;

    private OrganizationIdentity organizationIdentity;
    private BankDetail bankDetail;
    private String organizationBankDetailId;
    private String bankDetailId ;
    private String organizationIdentityId;
    @Autowired
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Autowired
    private BankDetailOutputPort bankDetailOutputPort;

    @BeforeAll
    void setup() {
        organizationIdentity = OrganizationIdentity.builder()
                .id(UUID.randomUUID().toString())
                .name("Test Org")
                .email("mak@email.com")
                .requestedInvitationDate(LocalDateTime.now())
                .organizationType(OrganizationType.COOPERATE)
                .build();
        bankDetail = BankDetail.builder()
                .bankName("Test Account")
                .bankNumber("1234567890")
                .activationStatus(ActivationStatus.APPROVED)
                .build();
        try {

            bankDetail = bankDetailOutputPort.save(bankDetail);
            organizationIdentityOutputPort.save(organizationIdentity);
            bankDetailId = bankDetail.getId();
            organizationIdentityId = organizationIdentity.getId();
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    @Order(1)
    void saveOrganizationBankDetail() throws MeedlException {
        organizationIdentity.setId(organizationIdentityId);
        bankDetail.setId(bankDetailId);
        OrganizationBankDetail organizationBankDetail = OrganizationBankDetail.builder()
                .organizationIdentity(organizationIdentity)
                .bankDetail(bankDetail)
                .build();

        OrganizationBankDetail savedOrganizationBankDetail = organizationBankDetailOutputPort.save(organizationBankDetail);
        organizationBankDetailId = savedOrganizationBankDetail.getId();
        log.info("Id of organization bank detail {}", organizationIdentityId);

        assertThat(savedOrganizationBankDetail.getOrganizationIdentity().getId()).isEqualTo(organizationIdentity.getId());
        assertThat(savedOrganizationBankDetail.getBankDetail().getId()).isEqualTo(bankDetail.getId());

        List<BankDetail> bankDetails = organizationBankDetailOutputPort.findAllBankDetailOfOrganization(organizationIdentity);
        assertThat(bankDetails).hasSize(1);
    }

    @Test
    @Order(2)
    void findApprovedBankDetailByOrganizationId() throws MeedlException {

        log.info("Organization id {}", organizationIdentity.getId());
        OrganizationBankDetail foundOrganizationBankDetail = organizationBankDetailOutputPort.findApprovedBankDetailByOrganizationId(organizationIdentity);

        assertThat(foundOrganizationBankDetail).isNotNull();
        assertThat(foundOrganizationBankDetail.getBankDetail().getId()).isEqualTo(bankDetail.getId());
    }

    @Test
    @Order(3)
    void findAllBankDetailOfOrganization() throws MeedlException {

        List<BankDetail> results = organizationBankDetailOutputPort.findAllBankDetailOfOrganization(organizationIdentity);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(bankDetail.getId());
    }

    @AfterAll
    void tearDown() throws MeedlException {
        organizationBankDetailOutputPort.deleteById(organizationBankDetailId);
        organizationIdentityOutputPort.delete(organizationIdentityId);
        bankDetailOutputPort.deleteById(bankDetailId);
        log.info("Deleted all after test");
    }
}
