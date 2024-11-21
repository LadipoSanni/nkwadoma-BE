package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.*;
import java.time.*;
import java.util.*;

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
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    @Mock
    private  LoanBreakdownOutputPort loanBreakdownOutputPort;
    private int pageSize = 2;
    private int pageNumber = 1;

    private ProgramCohort programCohort;
    private Cohort elites;
    private Loanee firstLoanee;
    private final String mockId = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
    private UserIdentity loaneeUserIdentity;
    private LoaneeLoanDetail loaneeLoanDetails;
    private LoanBreakdown loanBreakdown;


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


    }


    @Test
    void addLoaneeToCohort() {
        try {
            when(cohortOutputPort.findCohort(mockId)).thenReturn(elites);
            when(identityManagerOutputPort.createUser(loaneeUserIdentity)).thenReturn(loaneeUserIdentity);
            when(userIdentityOutputPort.save(loaneeUserIdentity)).thenReturn(loaneeUserIdentity);
            when(loanBreakdownOutputPort.saveAll(anyList(),any(LoaneeLoanDetail.class))).thenReturn(List.of(loanBreakdown));
            when(loaneeLoanDetailsOutputPort.save(any())).thenReturn(loaneeLoanDetails);
            when(cohortOutputPort.save(any())).thenReturn(elites);
            when(loaneeOutputPort.save(any())).thenReturn(firstLoanee);
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
    void cannotFindLoaneeWithNullLoaneeId(){
        assertThrows(MeedlException.class,()->loaneeService.viewLoaneeDetails(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"7837783-jjduydsbghew87ew-ekyuhjuhdsj"})
    void cannotFindLoaneeWithInvalidUuid(String id){
        assertThrows(MeedlException.class,() -> loaneeService.viewLoaneeDetails(id));
    }
}







