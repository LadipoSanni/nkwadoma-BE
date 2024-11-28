package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendLoaneeEmailUsecase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanReferralOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import lombok.extern.slf4j.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.*;
import java.time.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class LoaneeServiceTest {

    @InjectMocks
    private LoaneeService loaneeService;
    @Mock
    private LoaneeOutputPort loaneeOutputPort;
    @Mock
    private CohortOutputPort cohortOutputPort;
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private IdentityManagerOutputPort identityManagerOutputPort;
    @Mock
    private OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    @Mock
    private  LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    @Mock
    private LoanBreakdownOutputPort loanBreakdownOutputPort;
    @Mock
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Mock
    private SendLoaneeEmailUsecase loaneeEmailUsecase;
    @Mock
    private LoanReferralOutputPort loanReferralOutputPort;
    private int pageSize = 2;
    private int pageNumber = 1;

    private ProgramCohort programCohort;
    private Cohort elites;
    private Loanee firstLoanee;
    private final String mockId = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
    private UserIdentity loaneeUserIdentity;
    private LoaneeLoanDetail loaneeLoanDetails;
    private LoanBreakdown loanBreakdown;
    private LoanReferral loanReferral;
    private OrganizationIdentity organizationIdentity;
    private OrganizationEmployeeIdentity organizationEmployeeIdentity;


    @BeforeEach
    void setUpLoanee() {
        loaneeUserIdentity = UserIdentity.builder()
                .email("qudus55@gmail.com")
                .firstName("qudus")
                .lastName("lekan")
                .createdBy("fd099d9f-8d17-46dd-a04b-32c205e33503")
                .role(IdentityRole.LOANEE)
                .createdAt(LocalDateTime.now().toString())
                .build();

        firstLoanee = new Loanee();
        firstLoanee.setId(mockId);
        firstLoanee.setUserIdentity(loaneeUserIdentity);
        firstLoanee.setCreatedBy(mockId);
        firstLoanee.setCohortId(mockId);

        LoaneeLoanDetail loaneeLoanDetail = new LoaneeLoanDetail();
        loaneeLoanDetail.setInitialDeposit(BigDecimal.valueOf(100));
        loaneeLoanDetail.setAmountRequested(BigDecimal.valueOf(100));

        firstLoanee.setLoaneeLoanDetail(loaneeLoanDetail);


        loanBreakdown = new LoanBreakdown();
        loanBreakdown.setCurrency("usd");
        loanBreakdown.setItemAmount(BigDecimal.valueOf(30000));
        loanBreakdown.setItemName("juno");


        loaneeLoanDetails = new LoaneeLoanDetail();
        loaneeLoanDetails.setAmountRequested(BigDecimal.valueOf(3000));
        loaneeLoanDetails.setInitialDeposit(BigDecimal.valueOf(100));
        loaneeLoanDetail.setLoanBreakdown(List.of(loanBreakdown));

        firstLoanee.setLoaneeLoanDetail(loaneeLoanDetail);
        firstLoanee.setCreatedAt(LocalDateTime.now());

        elites = new Cohort();
        elites.setId(mockId);
        elites.setStartDate(LocalDate.of(2024, 10, 18));
        elites.setProgramId(mockId);
        elites.setName("Elite");
        elites.setCreatedBy(mockId);
        elites.setLoanBreakdowns(List.of(loanBreakdown));
        elites.setTuitionAmount(BigDecimal.valueOf(4000000));
        elites.setTotalCohortFee(BigDecimal.valueOf(4000000));

        programCohort = new ProgramCohort();
        programCohort.setCohort(elites);
        programCohort.setProgramId(mockId);
        programCohort.setId(mockId);

        loanReferral = new LoanReferral();
        loanReferral.setLoanee(firstLoanee);
        loanReferral.setLoanReferralStatus(LoanReferralStatus.PENDING);

        organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
        organizationEmployeeIdentity.setId(mockId);
        organizationEmployeeIdentity.setMeedlUser(loaneeUserIdentity);
        organizationEmployeeIdentity.setOrganization(mockId);

        organizationIdentity = new OrganizationIdentity();
        organizationIdentity.setId(organizationEmployeeIdentity.getOrganization());
        organizationIdentity.setName("Semicolon");
        organizationIdentity.setOrganizationEmployees(List.of(organizationEmployeeIdentity));
        organizationIdentity.setCreatedBy(mockId);


    }


    @Test
    void addLoaneeToCohort() throws MeedlException {
        OrganizationEmployeeIdentity mockEmployeeIdentity = new OrganizationEmployeeIdentity();
        mockEmployeeIdentity.setId(mockId);
        when(cohortOutputPort.findCohort(mockId)).thenReturn(elites);
        when(identityManagerOutputPort.createUser(loaneeUserIdentity)).thenReturn(loaneeUserIdentity);
        when(userIdentityOutputPort.save(loaneeUserIdentity)).thenReturn(loaneeUserIdentity);
        when(loanBreakdownOutputPort.saveAll(List.of(loanBreakdown),loaneeLoanDetails)).thenReturn(List.of(loanBreakdown));
        when(loaneeLoanDetailsOutputPort.save(any())).thenReturn(loaneeLoanDetails);
        when(cohortOutputPort.save(any())).thenReturn(elites);
        when(loaneeOutputPort.save(any())).thenReturn(firstLoanee);
        try {
            Loanee loanee = loaneeService.addLoaneeToCohort(firstLoanee);
            assertEquals(firstLoanee.getUserIdentity().getFirstName(), loanee.getUserIdentity().getFirstName());
            verify(loaneeOutputPort, times(1)).save(firstLoanee);
            verify(cohortOutputPort, times(1)).save(any(Cohort.class));
        } catch (MeedlException exception) {
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

    @Test
    void loaneeAmountRequestedCannotBeMoreThanCohortTotalFee() throws MeedlException {
        loaneeLoanDetails.setAmountRequested(BigDecimal.valueOf(7000));
        elites.setTotalCohortFee(BigDecimal.valueOf(200));
        firstLoanee.setLoaneeLoanDetail(loaneeLoanDetails);
        when(cohortOutputPort.findCohort(mockId)).thenReturn(elites);
        assertThrows(MeedlException.class, () -> loaneeService.addLoaneeToCohort(firstLoanee));
    }

    @Test
    void loaneeInitialDepositCannotBeGreaterThanCohortTotalFee() throws MeedlException {
        loaneeLoanDetails.setInitialDeposit(BigDecimal.valueOf(7000));
        elites.setTotalCohortFee(BigDecimal.valueOf(200));
        firstLoanee.setLoaneeLoanDetail(loaneeLoanDetails);
        when(cohortOutputPort.findCohort(mockId)).thenReturn(elites);
        assertThrows(MeedlException.class, () -> loaneeService.addLoaneeToCohort(firstLoanee));
    }


    @Test
    void cohortTutionHaveToBeUpdatedBeforeAddingALoaneeToACohort() throws MeedlException {
        elites.setTuitionAmount(null);
        when(cohortOutputPort.findCohort(mockId)).thenReturn(elites);
        assertThrows(MeedlException.class, () -> loaneeService.addLoaneeToCohort(firstLoanee));
    }


    @Test
    void cannotAddLoaneeToACohortWithExistingLoaneeEmail() throws MeedlException {
        when(loaneeOutputPort.findByLoaneeEmail(loaneeUserIdentity.getEmail())).thenReturn(firstLoanee);
        assertThrows(MeedlException.class, () -> loaneeService.addLoaneeToCohort(firstLoanee));
    }

    @Test
    void findLoanee(){
        Loanee loanee = new Loanee();
        try {
             when(loaneeOutputPort.findLoaneeById(mockId)).thenReturn(firstLoanee);
             loanee = loaneeService.viewLoaneeDetails(mockId);
        } catch (MeedlException exception) {
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertEquals(loanee.getUserIdentity().getEmail(),firstLoanee.getUserIdentity().getEmail());
    }

    @Test
    void viewAllLoaneeInCohort() throws MeedlException {
        when(loaneeOutputPort.findAllLoaneeByCohortId(mockId,pageSize,pageNumber)).
                thenReturn(new PageImpl<>(List.of(firstLoanee)));
        Page<Loanee> loanees = loaneeService.viewAllLoaneeInCohort(mockId,pageSize,pageNumber);
        assertEquals(1,loanees.toList().size());
    }

    @Test
    void viewAllLoaneeInCohortWithNullId() throws MeedlException {
        assertThrows(MeedlException.class, ()-> loaneeService.viewAllLoaneeInCohort(null,pageSize,pageNumber));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,StringUtils.SPACE})
    void viewAllLoaneeInCohortWithEmptyId(String cohortId) throws MeedlException {
        assertThrows(MeedlException.class, ()-> loaneeService.viewAllLoaneeInCohort(cohortId,pageSize,pageNumber));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-id"})
    void viewAllLoaneeInCohortWithInvalidId(String cohortId) throws MeedlException {
        assertThrows(MeedlException.class, ()-> loaneeService.viewAllLoaneeInCohort(cohortId,pageSize,pageNumber));
    }
    @Test
    void referTrainee(){
        try{
            firstLoanee.setLoaneeStatus(LoaneeStatus.ADDED);
            when(loaneeOutputPort.findLoaneeById(firstLoanee.getId())).thenReturn(firstLoanee);
            when(cohortOutputPort.findCohort(firstLoanee.getCohortId())).thenReturn(elites);
            when(loaneeOutputPort.findAllLoaneesByCohortId(elites.getId())).thenReturn(List.of(firstLoanee));
            when(organizationEmployeeIdentityOutputPort.findByEmployeeId(mockId))
                    .thenReturn(organizationEmployeeIdentity);
            when(organizationIdentityOutputPort.findById(mockId)).
                    thenReturn(organizationIdentity);
            when(loaneeOutputPort.save(firstLoanee)).thenReturn(firstLoanee);
            when(cohortOutputPort.save(elites)).thenReturn(elites);
            when(loanReferralOutputPort.createLoanReferral(firstLoanee)).thenReturn(loanReferral);
            LoanReferral loanReferral = loaneeService.referLoanee(firstLoanee.getId());
            assertEquals(loanReferral.getLoanee().getUserIdentity().getFirstName()
                    , firstLoanee.getUserIdentity().getFirstName());
        } catch (MeedlException exception) {
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
        }

    }

    @Test
    void cannotFindLoaneeWithNullLoaneeId(){
        assertThrows(MeedlException.class,()->loaneeService.viewLoaneeDetails(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"7837783-jjduydsbghew87ew-ekyuhjuhdsj"})
    void cannotFindLoaneeWithInvalidUuid(String id){
        assertThrows(MeedlException.class,() -> loaneeService.viewLoaneeDetails(id));
    }

}







