package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loanManagement;


import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanReferralOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanReferralRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class LoanReferralPersistenceAdapterTest {

    @Autowired
    private LoanReferralOutputPort loanReferralOutputPort;
    @Autowired
    private IdentityManagerOutputPort identityManagerOutputPort;
    @Autowired
    private UserIdentityOutputPort identityOutputPort;
    @Autowired
    private LoanBreakdownOutputPort loanBreakdownOutputPort;
    @Autowired
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    @Autowired
    private LoaneeOutputPort loaneeOutputPort;
    @Autowired
    private LoanReferralRepository loanReferralRepository;
    private Loanee loanee;
    private UserIdentity userIdentity;
    private LoaneeLoanDetail loaneeLoanDetail;
    private LoanBreakdown loanBreakdown;
    private String id = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
    private String loanReferralId ;


    @BeforeAll
    void setUp() {
        userIdentity = UserIdentity.builder().email("qudus55@gmail.com").firstName("qudus").lastName("lekan")
                .createdBy(id).role(IdentityRole.LOANEE).alternateContactAddress("312 semicolon africa")
                .alternatePhoneNumber("09079447913").alternateEmail("adeshina22@gmail,com").build();
        loaneeLoanDetail = LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(4000))
                .initialDeposit(BigDecimal.valueOf(200)).build();
        loanBreakdown = LoanBreakdown.builder().itemName("bread").itemAmount(BigDecimal.valueOf(34))
                .currency("usd").build();
//        loanee = Loanee.builder().cohortId(id).build();
        try {
            userIdentity = identityManagerOutputPort.createUser(userIdentity);
            userIdentity = identityOutputPort.save(userIdentity);
            loaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
            List<LoanBreakdown> loanBreakdownList = loanBreakdownOutputPort.saveAll(List.of(loanBreakdown),loaneeLoanDetail);
            loaneeLoanDetail.setLoanBreakdown(loanBreakdownList);
            loanee = Loanee.builder().cohortId(id).createdBy(userIdentity.getId()).userIdentity(userIdentity).loaneeLoanDetail(loaneeLoanDetail).build();
            loanee = loaneeOutputPort.save(loanee);
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
    }


    @Test
    @Order(1)
    void saveLoanReferral() {
        LoanReferral loanReferral = new LoanReferral();
        try {
             loanReferral = loanReferralOutputPort.createLoanReferral(loanee);
             loanReferralId = loanReferral.getId();
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
        assertEquals(loanReferral.getLoanee().getId(),loanee.getId());
    }

    @Test
    void saveLoanReferralWithNullLoanee(){
        assertThrows(MeedlException.class, ()-> loanReferralOutputPort.createLoanReferral(null));
    }

    @AfterAll
    void tearDown() throws MeedlException {
        loanReferralRepository.deleteById(loanReferralId);
        loaneeOutputPort.deleteLoanee(loanee.getId());
        identityOutputPort.deleteUserById(userIdentity.getId());
        identityManagerOutputPort.deleteUser(userIdentity);
    }

}
