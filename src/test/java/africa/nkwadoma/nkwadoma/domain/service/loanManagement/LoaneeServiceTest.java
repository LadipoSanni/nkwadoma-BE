package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateOrganizationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loan.LoaneeUsecase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.ProgramCohortException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class LoaneeServiceTest {

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
    private  LoanBreakdownOutputPort loanBreakdownOutputPort;

    private ProgramCohort programCohort;
    private Cohort elites;

    private Loanee firstLoanee;
    private String mockId = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
    private UserIdentity loaneeUserIdentity;
    private LoaneeLoanDetail loaneeLoanDetails;
    private LoanBreakdown loanBreakdown;


    @BeforeEach
    void setUpLoanee(){
         loaneeUserIdentity = UserIdentity.builder()
                    .email("qudus55@gmail.com")
                    .firstName("qudus")
                    .lastName("lekan")
                    .role(IdentityRole.LOANEE)
                    .createdAt(LocalDateTime.now().toString())
                    .build();

            firstLoanee = new Loanee();
            firstLoanee.setLoanee(loaneeUserIdentity);
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
        elites.setStartDate(LocalDateTime.of(2024,10,18,9,43));
        elites.setExpectedEndDate(LocalDateTime.of(2024,11,18,9,43));
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
    void addLoaneeToCohort() throws MeedlException {
        OrganizationEmployeeIdentity mockEmployeeIdentity = new OrganizationEmployeeIdentity();
        mockEmployeeIdentity.setId(mockId);
        when(cohortOutputPort.findCohort(mockId)).thenReturn(elites);
        when(identityManagerOutputPort.createUser(loaneeUserIdentity)).thenReturn(loaneeUserIdentity);
        when(userIdentityOutputPort.save(loaneeUserIdentity)).thenReturn(loaneeUserIdentity);
        when(loanBreakdownOutputPort.saveAll(List.of(loanBreakdown))).thenReturn(List.of(loanBreakdown));
        when(loaneeLoanDetailsOutputPort.save(any())).thenReturn(loaneeLoanDetails);
        when(cohortOutputPort.save(any())).thenReturn(elites);
        when(loaneeOutputPort.save(any())).thenReturn(firstLoanee);
        try {
            Loanee loanee = loaneeService.addLoaneeToCohort(firstLoanee);
            assertEquals(firstLoanee.getLoanee().getFirstName(), loanee.getLoanee().getFirstName());
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
        assertThrows(MeedlException.class,()->loaneeService.addLoaneeToCohort(firstLoanee));
    }

    @Test
    void loaneeInitialDepositCannotBeGreaterThanCohortTotalFee() throws MeedlException {
        loaneeLoanDetails.setInitialDeposit(BigDecimal.valueOf(7000));
        elites.setTotalCohortFee(BigDecimal.valueOf(200));
        firstLoanee.setLoaneeLoanDetail(loaneeLoanDetails);
        when(cohortOutputPort.findCohort(mockId)).thenReturn(elites);
        assertThrows(MeedlException.class,()->loaneeService.addLoaneeToCohort(firstLoanee));
    }


    @Test
    void cohortTutionHaveToBeUpdatedBeforeAddingALoaneeToACohort() throws MeedlException {
        elites.setTuitionAmount(null);
        when(cohortOutputPort.findCohort(mockId)).thenReturn(elites);
        assertThrows(MeedlException.class,()->loaneeService.addLoaneeToCohort(firstLoanee));
    }


    @Test
    void cannotAddLoaneeToACohortWithExistingLoaneeEmail() throws MeedlException {
        when(loaneeOutputPort.findByLoaneeEmail(loaneeUserIdentity.getEmail())).thenReturn(firstLoanee);
        assertThrows(MeedlException.class,()->loaneeService.addLoaneeToCohort(firstLoanee));
    }


}







