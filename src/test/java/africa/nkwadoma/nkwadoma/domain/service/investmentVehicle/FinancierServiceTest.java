package africa.nkwadoma.nkwadoma.domain.service.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.test.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class FinancierServiceTest {
    @InjectMocks
    private FinancierService financierService;
    private Financier financier;
    private UserIdentity userIdentity;
    @Mock
    private FinancierOutputPort financierOutputPort;

    @BeforeEach
    void setUp(){
        String testId = "efc9c7ae-9954-408f-9460-fcb6cf5efb3d";
        userIdentity = TestData.createTestUserIdentity("financieremailtest@mail.com",testId);
        financier = TestData.buildFinancierIndividual(userIdentity);
        financier.setId(testId);
        financier.setInvestmentVehicleId(testId);
    }

    @Test
    public void inviteFinancier() {
        Financier response;
        try {
            when(financierOutputPort.saveFinancier(financier)).thenReturn(financier);
            response = financierService.inviteFinancier(financier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(response);
        assertNotNull(response.getIndividual());
        assertNotNull(response.getId());
        assertEquals(financier.getIndividual().getId(), response.getIndividual().getId());
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinancierWithInvalidFirstName(String name)  {
        userIdentity.setFirstName(name);
        financier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierService.inviteFinancier(financier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinancierWithInvalidLastName(String name){
        userIdentity.setLastName(name);
        financier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierService.inviteFinancier(financier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt", "ead0f7cb-5483-4bb8-b271-813970a9c368"})
    public void inviteFinancierWithInvalidEmail(String email){
        userIdentity.setEmail(email);
        financier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierService.inviteFinancier(financier));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt"})
    public void inviteFinancierWithInvalidCreatedBy(String invitedBy){
        financier.setInvitedBy(invitedBy);
        assertThrows( MeedlException.class,()-> financierService.inviteFinancier(financier));
    }
    @Test
    public void inviteFinancierWithNullInvestmentVehicleFinancier() {
        assertThrows(MeedlException.class,()-> financierService.inviteFinancier(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinanciersWithInvalidFirstName(String name){
        userIdentity.setFirstName(name);
        financier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierService.inviteFinancier(financier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinanciersWithInvalidLastName(String name){
        userIdentity.setLastName(name);
        financier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierService.inviteFinancier(financier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt", "ead0f7cb-5483-4bb8-b271-813970a9c368"})
    public void inviteFinanciersWithInvalidEmail(String email){
        userIdentity.setEmail(email);
        financier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierService.inviteFinancier(financier));
    }
    @Test
    void viewAllFinanciers(){
        Page<Financier> financiersPage = null;
        try {
            when(financierOutputPort.viewAllFinancier(financier))
                    .thenReturn(new PageImpl<>(List.of(financier)));
            financiersPage = financierService.viewAllFinancier(financier);
            verify(financierOutputPort, times(1)).viewAllFinancier(financier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(financiersPage);
        assertNotNull(financiersPage.getContent());
        List<Financier> financiers = financiersPage.toList();
        assertEquals(1, financiers.size());
    }
    @Test
    void findFinancierById() {
        Financier foundFinancier = null;
        try {
            when(financierOutputPort.findFinancierById(financier.getId())).thenReturn(financier );
            foundFinancier = financierService.viewFinancierDetail(financier.getId());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinancier);
        assertEquals(financier.getId(), foundFinancier.getId());
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "ndnifeif"})
    void findFinancierByInvalidId(String invalidId) {
        assertThrows(MeedlException.class, ()-> financierService.viewFinancierDetail(invalidId));
    }
    @Test
    void viewFinanciersWithNull(){
        assertThrows(MeedlException.class,()-> financierService.viewAllFinancier(null));
    }
    @Test
    public void viewAllFinancierInInvestmentVehicle() {

    }
    @Test
    void viewAllFinancierInVehicleWithNull(){
//        assertThrows(MeedlException.class,()-> financierService.viewAllFinanciersInInvestmentVehicle(null));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "hidhfj"})
    void viewAllFinanciersInInvestmentVehicleWithInvalidVehicleId(String invalidId) {
        financier.setInvestmentVehicleId(invalidId);
//        assertThrows(MeedlException.class,()-> financierService.viewAllFinanciersInInvestmentVehicle(financier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt"})
    public void inviteFinancierWithInvalidOrNonExistingInvestmentVehicleId(String investmentVehicleId){
        financier.setInvestmentVehicleId(investmentVehicleId);
        assertThrows( MeedlException.class,()-> financierService.inviteFinancier(financier));
    }
    @Test
    public void inviteFinancierThatAlreadyExistOnThePlatform() {

//    financierOutputPort.inviteFinancier(financier);
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
