package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.InvestmentVehicleFinancierEntity;
import africa.nkwadoma.nkwadoma.test.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class FinancierAdapterTest {
    @Autowired
    private FinancierOutputPort financierOutputPort;
    private InvestmentVehicleFinancier investmentVehicleFinancier;
    private UserIdentity userIdentity;
    private UserIdentity userIdentity2;

    @BeforeEach
    void setUp(){
        userIdentity = TestData.createTestUserIdentity("financieremailtest@mail.com","efc9c7ae-9954-408f-9460-fcb6cf5efb3d");
        userIdentity2 = TestData.createTestUserIdentity("financieremailtest@mail.com","efc9c7ae-9954-408f-9460-fcb6cf5efb3d");
        investmentVehicleFinancier = TestData.buildInvestmentVehicleFinancierIndividual(userIdentity);

    }
    @Test
    public void inviteFinancier() {
        String response;
        try {
            response = financierOutputPort.inviteFinancier(investmentVehicleFinancier);
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
        investmentVehicleFinancier.setIndividuals(List.of(userIdentity));
        assertThrows( MeedlException.class,()-> financierOutputPort.inviteFinancier(investmentVehicleFinancier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinancierWithInvalidLastName(String name){
        userIdentity.setLastName(name);
        investmentVehicleFinancier.setIndividuals(List.of(userIdentity));
        assertThrows( MeedlException.class,()-> financierOutputPort.inviteFinancier(investmentVehicleFinancier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt", "ead0f7cb-5483-4bb8-b271-813970a9c368"})
    public void inviteFinancierWithInvalidEmail(String email){
        userIdentity.setEmail(email);
        investmentVehicleFinancier.setIndividuals(List.of(userIdentity));
        assertThrows( MeedlException.class,()-> financierOutputPort.inviteFinancier(investmentVehicleFinancier));
    }
    @Test
    public void inviteFinancierWithNullInvestmentVehicleFinancier() {
        assertThrows(MeedlException.class,()-> financierOutputPort.inviteFinancier(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinanciersWithInvalidFirstName(String name){
        userIdentity.setFirstName(name);
        investmentVehicleFinancier.setIndividuals(List.of(userIdentity, userIdentity));
        assertThrows( MeedlException.class,()-> financierOutputPort.inviteFinancier(investmentVehicleFinancier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinanciersWithInvalidLastName(String name){
        userIdentity.setLastName(name);
        investmentVehicleFinancier.setIndividuals(List.of(userIdentity, userIdentity));
        assertThrows( MeedlException.class,()-> financierOutputPort.inviteFinancier(investmentVehicleFinancier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt", "ead0f7cb-5483-4bb8-b271-813970a9c368"})
    public void inviteFinanciersWithInvalidEmail(String email){
        userIdentity.setEmail(email);
        investmentVehicleFinancier.setIndividuals(List.of(userIdentity, userIdentity2));
        assertThrows( MeedlException.class,()-> financierOutputPort.inviteFinancier(investmentVehicleFinancier));
    }
}