package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loanmanagement;


import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanBreakDownOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    @BeforeAll
    void setUpLoanee(){
        userIdentity = UserIdentity.builder().id(id).email("lekan@gmail.com").firstName("qudus").lastName("lekan")
                .createdBy(id).role(IdentityRole.LOANEE).build();
        loaneeLoanDetail = LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(4000))
                .initialDeposit(BigDecimal.valueOf(200)).build();
        try{
            userIdentity = identityOutputPort.save(userIdentity);
            loaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
            loanee = Loanee.builder().cohortId(id).userIdentity(userIdentity).loaneeLoanDetail(loaneeLoanDetail).build();
            loanee = loaneeOutputPort.save(loanee);
            loaneeId = loanee.getId();
            loaneeLoanDetailsId = loaneeLoanDetail.getId();
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
    }

    @BeforeEach
    void setUp(){
        loaneeLoanBreakdown = new LoaneeLoanBreakdown();
        loaneeLoanBreakdown.setLoaneeLoanBreakdownId(id);
        loaneeLoanBreakdown.setCurrency("USD");
        loaneeLoanBreakdown.setItemAmount(BigDecimal.valueOf(4000));
        loaneeLoanBreakdown.setItemName("juno");
    }

    @Test
    @Order(1)
    void saveLoaneeLoanBreakDown(){
        try {
            loaneeLoanBreakdowns = loaneeLoanBreakDownOutputPort.saveAll(List.of(loaneeLoanBreakdown),loanee);
        }catch (MeedlException e){
            log.error(e.getMessage());
        }
        assertEquals(loaneeLoanBreakdowns.get(0).getLoanee().getUserIdentity().getFirstName(),
                loanee.getUserIdentity().getFirstName());
    }


    @Test
    void cannotSaveWithNegativeAmount(){
        loaneeLoanBreakdown.setItemAmount(BigDecimal.valueOf(-4000));
        assertThrows(MeedlException.class, () ->loaneeLoanBreakDownOutputPort.saveAll(List.of(loaneeLoanBreakdown),loanee));
    }

    @Test
    void cannotSaveWithNullAmount(){
        loaneeLoanBreakdown.setItemAmount(null);
        assertThrows(MeedlException.class, () ->loaneeLoanBreakDownOutputPort.saveAll(List.of(loaneeLoanBreakdown),loanee));
    }

    @Test
    void cannotSaveWithNullItemName(){
        loaneeLoanBreakdown.setItemName(null);
        assertThrows(MeedlException.class, () ->loaneeLoanBreakDownOutputPort.saveAll(List.of(loaneeLoanBreakdown),loanee));
    }

    @Test
    void cannotSaveWithNullBreakdownId(){
        loaneeLoanBreakdown.setLoaneeLoanBreakdownId(null);
        assertThrows(MeedlException.class, () ->loaneeLoanBreakDownOutputPort.saveAll(List.of(loaneeLoanBreakdown),loanee));
    }


    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY,"jdhjhdshjdshj"})
        void cannotSaveWithInvalidId(String invalidId){
            loaneeLoanBreakdown.setLoaneeLoanBreakdownId(invalidId);
            assertThrows(MeedlException.class,()-> loaneeLoanBreakDownOutputPort.saveAll(List.of(loaneeLoanBreakdown),loanee));
    }

    @AfterAll
    void cleanUp() throws MeedlException {
        loaneeLoanBreakDownOutputPort.deleteAll(List.of(loaneeLoanBreakdown));
        loaneeOutputPort.deleteLoanee(loaneeId);
        identityOutputPort.deleteUserById(id);
        loaneeLoanDetailsOutputPort.delete(loaneeLoanDetailsId);
    }
}
