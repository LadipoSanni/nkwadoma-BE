package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.test.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class FinancierAdapterTest {
    @Autowired
    private FinancierOutputPort financierOutputPort;
    private Financier financier;
    private UserIdentity userIdentity;
    private UserIdentity userIdentity2;
    private InvestmentVehicle investmentVehicle;
    private InvestmentVehicleOutputPort investmentVehicleOutputPort;

    @BeforeEach
    void setUp(){
        userIdentity = TestData.createTestUserIdentity("financieremailtest@mail.com","efc9c7ae-9954-408f-9460-fcb6cf5efb3d");
        userIdentity2 = TestData.createTestUserIdentity("financieremailtest@mail.com","efc9c7ae-9954-408f-9460-fcb6cf5efb3d");
        financier = TestData.buildFinancierIndividual(userIdentity);
        investmentVehicle = TestData.buildInvestmentVehicle("FinancierVehicleForTest");
        try {
            foundInvestmentVehicle = investmentVehicleOutputPort.findByNameExcludingDraftStatus(investmentVehicle.getName());
            investmentVehicle = investmentVehicleOutputPort.save(investmentVehicle);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }

    }
    @Test
    public void inviteFinancier() {
        String response;
        try {
            response = financierOutputPort.inviteFinancier(financier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertEquals("Financier invited.", response);
    }
    @Test
    public void inviteFinancierTwiceToTheSameInvestmentVehicle(){

    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinancierWithInvalidFirstName(String name)  {
        userIdentity.setFirstName(name);
        financier.setIndividuals(List.of(userIdentity));
        assertThrows( MeedlException.class,()-> financierOutputPort.inviteFinancier(financier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinancierWithInvalidLastName(String name){
        userIdentity.setLastName(name);
        financier.setIndividuals(List.of(userIdentity));
        assertThrows( MeedlException.class,()-> financierOutputPort.inviteFinancier(financier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt", "ead0f7cb-5483-4bb8-b271-813970a9c368"})
    public void inviteFinancierWithInvalidEmail(String email){
        userIdentity.setEmail(email);
        financier.setIndividuals(List.of(userIdentity));
        assertThrows( MeedlException.class,()-> financierOutputPort.inviteFinancier(financier));
    }
    @Test
    public void inviteFinancierWithNullInvestmentVehicleFinancier() {
        assertThrows(MeedlException.class,()-> financierOutputPort.inviteFinancier(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinanciersWithInvalidFirstName(String name){
        userIdentity.setFirstName(name);
        financier.setIndividuals(List.of(userIdentity, userIdentity));
        assertThrows( MeedlException.class,()-> financierOutputPort.inviteFinancier(financier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinanciersWithInvalidLastName(String name){
        userIdentity.setLastName(name);
        financier.setIndividuals(List.of(userIdentity, userIdentity));
        assertThrows( MeedlException.class,()-> financierOutputPort.inviteFinancier(financier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt", "ead0f7cb-5483-4bb8-b271-813970a9c368"})
    public void inviteFinanciersWithInvalidEmail(String email){
        userIdentity.setEmail(email);
        financier.setIndividuals(List.of(userIdentity, userIdentity2));
        assertThrows( MeedlException.class,()-> financierOutputPort.inviteFinancier(financier));
    }
    @Test
    public void inviteFinancierThatAlreadyExistOnThePlatform() {

    financierOutputPort.inviteFinancier(financier);
    }
    @Test
    public void inviteFinancierThatDoesNotExistOnThePlatform() {

    }
    @Test
    public void inviteFinancierThatHasAlreadyBeenAddedToAnInvestmentVehicle() {

    }
    @Test
    public void inviteFinancierToNoneExistentInvestmentVehicle(){

    }
    @Test
    public void inviteLoaneeToBecomeAFinancier(){

    }
}