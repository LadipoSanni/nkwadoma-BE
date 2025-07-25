package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loanmanagement;


import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanBreakDownOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.UploadedStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class LoaneeLoanBreakDownPersistenceAdapterTest {

    @Autowired
    private LoaneeOutputPort loaneeOutputPort;
    @Autowired
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    @Autowired
    private UserIdentityOutputPort identityOutputPort;
    @Autowired
    private LoaneeLoanBreakDownOutputPort loaneeLoanBreakDownOutputPort;
    private UserIdentity userIdentity;
    private Loanee loanee;
    private LoaneeLoanBreakdown loaneeLoanBreakdown;
    private String id = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
    private LoaneeLoanDetail loaneeLoanDetail;
    private List<LoaneeLoanBreakdown> loaneeLoanBreakdowns;
    private String loaneeId;
    private String loaneeLoanDetailsId;
    private CohortLoanee cohortLoanee;
    private Cohort cohort;
    @Autowired
    private CohortOutputPort cohortOutputPort;
    @Autowired
    private CohortLoaneeOutputPort cohortLoaneeOutputPort;

    @BeforeAll
    void setUpLoanee(){
        userIdentity = UserIdentity.builder().id(id).email("lekan@gmail.com").firstName("qudus").lastName("lekan")
                .createdBy(id).role(IdentityRole.LOANEE).build();
        loaneeLoanDetail = LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(4000))
                .initialDeposit(BigDecimal.valueOf(200)).loanStartDate(LocalDateTime.now()).createdAt(LocalDateTime.now()).build();
        cohort = TestData.createCohortData("X-men",id,id,null,id);
        try{
            userIdentity = identityOutputPort.save(userIdentity);
            loaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
            loanee = Loanee.builder().cohortId(id).userIdentity(userIdentity).loaneeLoanDetail(loaneeLoanDetail).build();
            loanee = loaneeOutputPort.save(loanee);
            loaneeId = loanee.getId();
            loaneeLoanDetailsId = loaneeLoanDetail.getId();
            cohort = cohortOutputPort.save(cohort);
            cohortLoanee = CohortLoanee.builder().
                    loanee(loanee).cohort(cohort).createdBy(id).
                    loaneeLoanDetail(loaneeLoanDetail).createdAt(LocalDateTime.now())
                    .loaneeStatus(LoaneeStatus.ADDED).uploadedStatus(UploadedStatus.ADDED)
                    .onboardingMode(OnboardingMode.EMAIL_REFERRED).build();
            cohortLoanee = cohortLoaneeOutputPort.save(cohortLoanee);
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
    }

    @BeforeEach
    void setUp(){
        loaneeLoanBreakdown = new LoaneeLoanBreakdown();
        loaneeLoanBreakdown.setCurrency("USD");
        loaneeLoanBreakdown.setItemAmount(BigDecimal.valueOf(4000));
        loaneeLoanBreakdown.setItemName("juno");
    }

    @Test
    @Order(1)
    void saveLoaneeLoanBreakDown(){
        try {
            loaneeLoanBreakdowns = loaneeLoanBreakDownOutputPort.saveAll(List.of(loaneeLoanBreakdown),cohortLoanee);
        }catch (MeedlException e){
            log.error(e.getMessage());
        }
        assertEquals(loaneeLoanBreakdowns.get(0).getCohortLoanee().getLoanee().getUserIdentity().getFirstName(),
                loanee.getUserIdentity().getFirstName());
    }


    @Test
    void cannotSaveWithNegativeAmount(){
        loaneeLoanBreakdown.setItemAmount(BigDecimal.valueOf(-4000));
        assertThrows(MeedlException.class, () ->loaneeLoanBreakDownOutputPort.saveAll(List.of(loaneeLoanBreakdown),cohortLoanee));
    }

    @Test
    void cannotSaveWithNullAmount(){
        loaneeLoanBreakdown.setItemAmount(null);
        assertThrows(MeedlException.class, () ->loaneeLoanBreakDownOutputPort.saveAll(List.of(loaneeLoanBreakdown),cohortLoanee));
    }

    @Test
    void cannotSaveWithNullItemName(){
        loaneeLoanBreakdown.setItemName(null);
        assertThrows(MeedlException.class, () ->loaneeLoanBreakDownOutputPort.saveAll(List.of(loaneeLoanBreakdown),cohortLoanee));
    }


    @AfterAll
    void cleanUp() throws MeedlException {
        loaneeLoanBreakDownOutputPort.deleteAll(loaneeLoanBreakdowns);
        List<LoaneeLoanBreakdown> loanBreakdowns = loaneeLoanBreakDownOutputPort.findAllLoaneeLoanBreakDownByCohortLoaneeId(cohortLoanee.getId());
        log.info("loan breakdown found {}", loanBreakdowns);

        cohortLoaneeOutputPort.delete(cohortLoanee.getId());
        cohortOutputPort.deleteCohort(cohort.getId());
        loaneeOutputPort.deleteLoanee(loaneeId);
        identityOutputPort.deleteUserById(id);
        loaneeLoanDetailsOutputPort.delete(loaneeLoanDetailsId);
    }
}
